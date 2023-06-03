package agentVagrant;

import java.io.IOException;
import java.net.ServerSocket;

import javax.swing.JOptionPane;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;

public class Main {

    public static void main(String[] args) {
        try (ServerSocket socket = new ServerSocket(0)) {
            int port = socket.getLocalPort();
            ProfileImpl p = new ProfileImpl();
            p.setParameter(Profile.MAIN_PORT, 10000 + "");
            p.setParameter(Profile.CONTAINER_NAME, "Main-Container" + port);
            ContainerController containerController = Runtime.instance().createMainContainer(p);
            JOptionPane.showMessageDialog(null,
                    "SEJA BEM-VINDO "
                            + "\n SMA - SISTEMA MULTIAGENTE");
            String readInput = JOptionPane.showInputDialog(null, "MENU MÁQUINA VIRTUAIS\n (1)Criar uma máquina\n (3)Sair do programa");
            int optionPaneInput = Integer.parseInt(readInput);
            while (optionPaneInput != 3) {
                String readInput2 = JOptionPane.showInputDialog(null,
                        "MENU MÁQUINA VIRTUAIS\n (1)Criar uma máquina\n (2)Destruir Máquina\n (3)Sair do programa");
                optionPaneInput = Integer.parseInt(readInput2);
                try {
                    PlatformController platformController = containerController.getPlatformController();
                    if (optionPaneInput == GlobalVars.CREATE_AGENT) {
                        try {
                            AgentController AgentStart = platformController.createNewAgent("AgentStart",
                                    "agentVagrant.AgentStart", null);
                            AgentStart.start();

                            AgentController AgentMonitor = platformController.createNewAgent("AgentMonitor",
                                    "agentVagrant.AgentMonitor", null);
                            AgentMonitor.start();
//                            Thread.sleep(140000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (optionPaneInput == GlobalVars.DESTOY_AGENT) {
                        try {
                            AgentController AgentDestroy = platformController.createNewAgent("AgentDestroy",
                                    "agentVagrant.AgentDestroy", null);
                            AgentDestroy.start();
//                            Thread.sleep(10000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else if (optionPaneInput == GlobalVars.EXIT_SYSTEM) {
                        JOptionPane.showMessageDialog(null, "SISTEMA ENCERRADO");
                        System.exit(3);
                    }
                } catch (ControllerException e1) {
                    e1.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
