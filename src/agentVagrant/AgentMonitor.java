package agentVagrant;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.StaleProxyException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

public class AgentMonitor extends Agent {
    private List<String> machines;
    private String selectedMachine;

    @Override
    protected void setup() {
        System.out.println("----AgentMonitor Starting----");

        // Iniciar o comportamento para listar as máquinas criadas pelo usuário
        addBehaviour(new ListMachinesBehaviour());
    }

    private class ListMachinesBehaviour extends OneShotBehaviour {
        public void action() {
            try {
                // Obter uma referência para o container do agente
                jade.wrapper.AgentContainer container = getContainerController();

                // Criar uma instância do AgentList no container
                jade.wrapper.AgentController agentListController = container.createNewAgent(
                        "AgentList" + System.currentTimeMillis(),
                        "agentVagrant.AgentList",
                        null);
                agentListController.start();

                // Aguardar um curto período para garantir que o AgentList tenha tempo para listar as máquinas
                Thread.sleep(1000);

                // Obter a lista de máquinas criadas
                machines = ((AgentList) agentListController.getO2AInterface(AgentList.class)).getMachineList();

                // Exibir a lista de máquinas para o usuário escolher
                System.out.println("---- List of Machines ----");
                for (int i = 0; i < machines.size(); i++) {
                    System.out.println((i + 1) + ". " + machines.get(i));
                }

                try (// Obter a escolha do usuário
                Scanner scanner = new Scanner(System.in)) {
                    int choice = -1;
                    while (choice < 1 || choice > machines.size()) {
                        System.out.print("Enter the number of the machine to monitor: ");
                        choice = scanner.nextInt();
                    }

                    // Salvar a máquina selecionada
                    selectedMachine = machines.get(choice - 1);
                }
                System.out.println("Selected machine: " + selectedMachine);

                // Iniciar o comportamento para receber dados da máquina selecionada
                addBehaviour(new ReceiveDataBehaviour());
            } catch (StaleProxyException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class ReceiveDataBehaviour extends CyclicBehaviour {
        public void action() {
            
            ACLMessage msg = receive();

            if (msg != null) {
                // Check message type (INFORM is used for data)
                if (msg.getPerformative() == ACLMessage.INFORM) {
                    // Get the message content (which contains the data)
                    String data = msg.getContent();

                    // Save the data to a file on the physical machine
                    saveDataToFile(data);

                    // Display the data in the monitor agent console
                    System.out.println("Received data from " + selectedMachine + ": " + data);
                }
            } else {
                // Caso não haja mensagem disponível no momento, bloquear o comportamento até que uma nova mensagem seja recebida
                block();
            }
        }
    }

    private void saveDataToFile(String data) {
        try {
            // Caminho e nome do arquivo para salvar os dados
            String filePath = "data.txt";

            // Abrir o arquivo para escrita
            FileWriter fileWriter = new FileWriter(filePath, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            // Escrever os dados no arquivo
            printWriter.println(data);

            // Fechar o arquivo
            printWriter.close();

            System.out.println("Data saved to file: " + filePath);
        } catch (IOException e) {
            System.out.println("An error occurred while saving data to file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}



//package agentVagrant;
//
//import static agentVagrant.GlobalVars.*;
//
//import java.io.BufferedReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//
//import jade.core.Agent;
//
//public class AgentMonitor extends Agent {
//
//	String r0, ler;
//	ProcessBuilder processBuilder;
//	Process pr;
//	BufferedReader reader;
//	String line;
//
//	@Override
//	protected void setup() {
//
//		String usuario = GlobalVars.control + "";
//		jade.lang.acl.ACLMessage TM0 = blockingReceive();
//		System.out.println(TM0.getContent());
//		ProcessBuilder processBuilder = new ProcessBuilder();
//		System.out.println("---- Inicio do Agente Monitor ----");
//		processBuilder.command("cmd.exe", "/c", "vagrant status");
//
//		try {
//			 String[] command4;
//            if (System.getProperty("os.name").toLowerCase().contains("win")) {
//                command4 = new String[]{"cmd.exe", "/c",
//                        "cd " + GlobalVars.vagrantDir + " && " + GlobalVars.vagrantCommand + " ssh -c 'sudo apt-get install dstat'"};
//            } else {
//                command4 = new String[]{GlobalVars.vagrantCommand, "cd", GlobalVars.vagrantDir, "&&", GlobalVars.vagrantCommand, "ssh -c 'sudo apt-get install dstat'"};
//            }
//
//			processBuilder = new ProcessBuilder(command4);
//			Process pr = processBuilder.start();
//			//pr.waitFor();
//			reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
//			while (pr.waitFor() == 0 && (line = reader.readLine()) != null) {
//				System.out.println("Entrou no loop");
//				System.out.println(line);
//			}
//
//			System.out.println("Dstat Instalado");
//
//			Runtime runtime = Runtime.getRuntime();
//			 if (System.getProperty("os.name").toLowerCase().contains("win")) {
//                runtime.exec("cmd.exe /c cd " + GlobalVars.vagrantDir + " && start cmd.exe /k \"" + GlobalVars.vagrantCommand + " ssh\"");
//            } else {
//                runtime.exec(GlobalVars.vagrantCommand + " cd " + GlobalVars.vagrantDir + " && " + GlobalVars.vagrantCommand + " ssh");
//            }
//            //pr.waitFor();
//
//			String[] command;
//            if (System.getProperty("os.name").toLowerCase().contains("win")) {
//                command = new String[]{"cmd.exe", "/c",
//                        "cd " + GlobalVars.vagrantDir + " && " + GlobalVars.vagrantCommand + " ssh -c 'dstat -cmdn '"};
//            } else {
//                command = new String[]{GlobalVars.vagrantCommand, "cd", GlobalVars.vagrantDir, "&&", GlobalVars.vagrantCommand, "ssh -c 'dstat -cmdn'"};
//            }
//
//    		processBuilder = new ProcessBuilder(command);
//
//			pr = processBuilder.start();
//			reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
//
//			line = "";
//			int count = 0;
//			// String cpu = "";
//			// float cpuFree = 0;
//			float cpuUsed = 0;
//			// float memFree = 0;
//			float memUsed = 0;
//			float mediaCpu = 0;
//			float mediaMem = 0;
//
//			while ((line = reader.readLine()) != null) {
//				line = line.trim().replaceAll("0;0", "");
//				line = line.trim().replaceAll("\\s+", " ");
//				line = line.trim().replaceAll("\\|+", " ");
//				line = line.trim().replaceAll("[a-zA-Z]+", "");
//				// System.out.println("line 4 "+line);
//
//				// line=line.trim().replaceAll(" ","P");
//				String[] parts = line.split(" ");
//				cpuUsed += Float.parseFloat(parts[0]);
//				cpuUsed += Float.parseFloat(parts[1]);
//				// cpuFree += Float.parseFloat(parts[2]);
//				memUsed += Float.parseFloat(parts[7]);
//				// memFree += Float.parseFloat(parts[10]);
//				count++;
//				mediaCpu = cpuUsed / count;
//				mediaMem = memUsed / count;
//
//				FileWriter arq = new FileWriter(GlobalVars.vagrantDir + "/MediaCpuMem" + usuario + ".txt");
//                PrintWriter gravarArq = new PrintWriter(arq);
//                gravarArq.println("Media de CPU usada =" + mediaCpu);
//                gravarArq.println("Media de Memoria usada em MegaBits =" + mediaMem);
//                arq.close();
//			}
//
//		} catch (IOException | InterruptedException e) {
//			e.printStackTrace();
//		}
//		System.out.println("---- Fim do Agente Monitor ----");
//		doDelete();
//	}
//}