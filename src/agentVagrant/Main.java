package agentVagrant;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import javax.swing.JOptionPane;

import static agentVagrant.GlobalVars.*;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;

public class Main {

    public static int controller;

    public static void main(String[] args) {
        try (ServerSocket socket = new ServerSocket(0)) {
            int port = socket.getLocalPort();
            ProfileImpl profile = new ProfileImpl();
            profile.setParameter(Profile.MAIN_PORT, 10000 + "");
            profile.setParameter(Profile.CONTAINER_NAME, "Main-Container" + port);
            ContainerController containerController = Runtime.instance().createMainContainer(profile);

            boolean[] exitSystem = { false };

            while (!exitSystem[0]) {
                try {
                    JOptionPane.showMessageDialog(null, "WELCOME \nSMA - Multi-agent System");
                    String readInput = JOptionPane.showInputDialog(null,
                            "VIRTUAL MACHINES MENU\n(1)Create new machine\n(2)List of machines\n(3)Monitor machine\n(4)Destroy machine\n(0)Exit system");
                    int optionPaneInput = Integer.parseInt(readInput);

                    PlatformController platformController = containerController.getPlatformController();
                    CompletableFuture<Void> agentFuture = CompletableFuture.runAsync(() -> {

                        switch (optionPaneInput) {
                            case 0: //Exit system
                                exitSystem[0] = true;
                                break;

                            case CREATE_MACHINES:
                                try {
                                    CountDownLatch latch = new CountDownLatch(1);
                                    Object[] agentArgs = new Object[]{latch};
                                    AgentController agentStart = platformController.createNewAgent(
                                            "AgentStart" + System.currentTimeMillis(),
                                            "agentVagrant.AgentStart",
                                            agentArgs);
                                    agentStart.start();
                                    latch.await();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;

                            case LIST_MACHINES:
                                try {
                                    CountDownLatch latch = new CountDownLatch(1);
                                    Object[] agentArgs = new Object[]{latch};
                                    AgentController agentList = platformController.createNewAgent(
                                            "AgentList" + System.currentTimeMillis(),
                                            "agentVagrant.AgentList",
                                            agentArgs);
                                    agentList.start();
                                    latch.await();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;

                            case MONITOR_MACHINES:
                                //Todo
                                break;

                            case DESTROY_MACHINES:
                            	 try {
                                     CountDownLatch latch = new CountDownLatch(1);
                                     Object[] agentArgs = new Object[]{latch};
                                     AgentController agentDestroy = platformController.createNewAgent(
                                             "AgentDestroy" + System.currentTimeMillis(),
                                             "agentVagrant.AgentDestroy",
                                             agentArgs);
                                     agentDestroy.start();
                                     latch.await();
                                 } catch (Exception e) {
                                     e.printStackTrace();
                                 }
                                 break;

                            default:
                                System.out.println("Invalid option. Type it again.");
                                break;
                        }
                    });

                    agentFuture.thenRun(() -> System.exit(0));

                } catch (ControllerException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}