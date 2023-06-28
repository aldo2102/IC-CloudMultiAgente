package agentVagrant;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.CompletableFuture;

import javax.swing.JOptionPane;

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
                    JOptionPane.showMessageDialog(null, "WELCOME \n SMA - SISTEMA MULTIAGENTE");
                    String readInput = JOptionPane.showInputDialog(null,
                            "VIRTUAL MACHINES MENU\n (1)Create new machine\n (2)List of machines\n (3)Monitor machine\n (4)Destroy machine\n (0)Exit system");
                    int optionPaneInput = Integer.parseInt(readInput);

                    PlatformController platformController = containerController.getPlatformController();
                    CompletableFuture<Void> agentFuture = CompletableFuture.runAsync(() -> {

                        switch (optionPaneInput) {
                            case 1://Create
                                try {
                                    AgentController agentStart = platformController.createNewAgent(
                                            "AgentStart" + System.currentTimeMillis(), "agentVagrant.AgentStart",
                                            null);
                                    agentStart.start();
                                    Thread.sleep(10000); 
                                    
                                    
                                    while (agentStart.getState().getCode() != 5) {
                                        Thread.sleep(10000);
                                    }
                                    
                                    AgentController agentMonitor = platformController.createNewAgent(
                                            "AgentMonitor" + System.currentTimeMillis(),
                                            "agentVagrant.AgentMonitor", null);
                                    agentMonitor.start();
                                    Thread.sleep(10000); 
                                    
                                 
                                    while (agentMonitor.getState().getCode() != 5) {
                                        Thread.sleep(10000);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;

                            case 2://List
                                try {
                                    AgentController agentList = platformController.createNewAgent(
                                            "AgentList" + System.currentTimeMillis(), "agentVagrant.AgentList",
                                            null);
                                    agentList.start();
                                    Thread.sleep(10000); 
                                    
                                    
                                    while (agentList.getState().getCode() != 5) {
                                        Thread.sleep(10000);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;

                            case 3:// monitor
                                break;

                            case 4: // Destroy
                            	
                            	 try {
                                     AgentController agentDestroy = platformController.createNewAgent(
                                             "AgentDestroy" + System.currentTimeMillis(), "agentVagrant.AgentDestroy",
                                             null);
                                     agentDestroy.start();
                                     Thread.sleep(10000); 
                                     
                                     
                                     while (agentDestroy.getState().getCode() != 5) {
                                         Thread.sleep(10000);
                                     }
                                 } catch (Exception e) {
                                     e.printStackTrace();
                                 }
                                 break;

                                

                            case 0: // Exit system
                            	exitSystem[0] = true;
                                break;

                            default:
                                System.out.println("Invalid option. Type it again.");
                                break;
                        }

                    });

                    agentFuture.thenRun(() -> {
                        System.exit(0);
                    });

                } catch (ControllerException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


//            JOptionPane.showMessageDialog(null,
//                    "SEJA BEM-VINDO "
//                            + "\n SMA - SISTEMA MULTIAGENTE");
//            String readInput = JOptionPane.showInputDialog(null, "MENU MÃ�QUINA VIRTUAIS\n (1)Criar uma mÃ¡quina\n (3)Sair do programa");
//            int optionPaneInput = Integer.parseInt(readInput);
//           
//           while (optionPaneInput != 3) {
//                String readInput2 = JOptionPane.showInputDialog(null,
//                        "MENU MÃ�QUINA VIRTUAIS\n (1)Criar uma mÃ¡quina\n (2)Destruir MÃ¡quina\n (3)Sair do programa");
//                optionPaneInput = Integer.parseInt(readInput2);
//                try {
//                    PlatformController platformController = containerController.getPlatformController();
//                    if (optionPaneInput == GlobalVars.CREATE_AGENT) {
//                        try {
//                        	//implementacao do System.currentTimeMillis() para que nao haja duplicidade de agentes
//                        	AgentController agentStart = platformController.createNewAgent("AgentStart" + System.currentTimeMillis(),
//                        	        "agentVagrant.AgentStart", null);
//                        	agentStart.start();
//
//
//                        	AgentController agentMonitor = platformController.createNewAgent("AgentMonitor" + System.currentTimeMillis(),
//                        	        "agentVagrant.AgentMonitor", null);
//                        	agentMonitor.start();
//
////                            Thread.sleep(140000);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    } else if (optionPaneInput == GlobalVars.DESTROY_AGENT) {
//                        try {
//
//                            AgentController AgentDestroy = platformController.createNewAgent("AgentDestroy"+ + System.currentTimeMillis(),
//                                    "agentVagrant.AgentDestroy", null);
//                            AgentDestroy.start();
////                            Thread.sleep(10000);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                    } else if (optionPaneInput == GlobalVars.EXIT_SYSTEM) {
//                        JOptionPane.showMessageDialog(null, "SISTEMA ENCERRADO");
//                        System.exit(3);
//                    }
//                } catch (ControllerException e1) {
//                    e1.printStackTrace();
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
