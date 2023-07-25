package agentVagrant;

import java.io.IOException;
import java.net.ServerSocket;
import java.time.Instant;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;

import static agentVagrant.GlobalVars.*;

public class Main {

    public static int controller = 0;

    public static void main(String[] args) {
        try (ServerSocket socket = new ServerSocket(0)) {
            int port = socket.getLocalPort();
            ProfileImpl profile = new ProfileImpl();
            profile.setParameter(Profile.MAIN_PORT, 10000 + "");
            profile.setParameter(Profile.CONTAINER_NAME, "Main-Container" + port);
            ContainerController containerController = Runtime.instance().createMainContainer(profile);

            Scanner scanner = new Scanner(System.in);

            System.out.println("""
                                        
                    |---------WELCOME---------|
                    |    Multi-agent System   |
                    |----------MENU-----------|
                    |  (1)  Create machine    |
                    |  (2)  List machines     |
                    |  (3)  Monitor machine   |
                    |  (4)  Destroy machine   |
                    |  (0)  Exit              |
                    |-------------------------|
                    """);
            System.out.println("Enter your choice:");
            int option = scanner.nextInt();
            scanner.nextLine();

            PlatformController platformController = containerController.getPlatformController();
            CompletableFuture<Void> agentFuture = CompletableFuture.runAsync(() -> {

                switch (option) {
                    case EXIT_SYSTEM:
                        System.exit(0);
                        break;

                    case CREATE_MACHINES:
                        try {
                            System.out.println("Enter the number of machines to create: ");
                            int numMachines = scanner.nextInt();
                            scanner.nextLine();
                            CountDownLatch latch = new CountDownLatch(numMachines);
                            UUID uuid = UUID.nameUUIDFromBytes(Instant.now().toString().getBytes());
                            for (int i = 0; i < numMachines; i++) {
                                Object[] agentArgs = new Object[]{latch};
                                AgentController agentStart = platformController.createNewAgent(
                                        uuid.toString() + Main.controller + System.currentTimeMillis(),
                                        "agentVagrant.AgentStart",
                                        agentArgs);
                                Main.controller++;
                                agentStart.start();
                            }
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
                        try {
                            CountDownLatch latch = new CountDownLatch(1);
                            Object[] agentArgs = new Object[]{latch};
                            AgentController agentMonitor = platformController.createNewAgent(
                                    "AgentMonitor" + System.currentTimeMillis(),
                                    "agentVagrant.AgentMonitor",
                                    agentArgs);
                            agentMonitor.start();
                            latch.await();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
//            }
        } catch (IOException | ControllerException e) {
            e.printStackTrace();
        }
    }
}
