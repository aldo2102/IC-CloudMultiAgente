package agentVagrant;

import jade.core.Agent;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class AgentDestroy extends Agent {
    private String osCommand;
    private String cmdPath;

    @Override
    protected void setup() {
        System.out.println("----AgentDestroy Starting----");

        // Obter uma referência para o container do agente
        AgentContainer container = getContainerController();

        try {
            // Criar uma instância de AgentList no container
            AgentController agentListController = container.createNewAgent("AgentList", "agentVagrant.AgentList", null);
            agentListController.start();

            // Aguardar o término do agente AgentList
            //agentListController.join();

            // Prompt user to enter the machine name to destroy
            String machineName = getUserInput("Enter the name of the machine to destroy: ");

            // Destroy the selected machine
            destroyMachine(machineName);

            System.out.println("----AgentDestroy Ended----");
            doDelete();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    private String getUserInput(String message) {
        System.out.print(message);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void destroyMachine(String machineName) {
        osCommand = GlobalVars.getOsCommand();
        cmdPath = GlobalVars.getCmdPath();

        try {
            // Executar o comando 'vagrant destroy' para destruir a máquina especificada
            String[] destroyMachineCommand = {osCommand, cmdPath, "vagrant", "destroy", machineName, "--force"};
            System.out.println("Destroying machine: " + Arrays.toString(destroyMachineCommand));
            ProcessBuilder pb = new ProcessBuilder(destroyMachineCommand);
            Process pr = pb.start();
            pr.waitFor();

            System.out.println("Machine " + machineName + " destroyed successfully.");
        } catch (IOException | InterruptedException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}







//package agentVagrant;
//
//import static agentVagrant.GlobalVars.*;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//
//import jade.core.Agent;
//
//import javax.swing.JOptionPane;
//
//public class AgentDestroy extends Agent {
//	ProcessBuilder pb;
//	Process pr;
//
//	@Override
//	protected void setup() {
//		JOptionPane.showMessageDialog(null, "PRONTO PARA DESTRUIR!", "ALERTA", JOptionPane.WARNING_MESSAGE);
//		String usuario;
//		usuario = JOptionPane.showInputDialog(null, "Escreva o nome da maquina que serÃ¡ destruÃ­da", "ALERTA",
//				JOptionPane.WARNING_MESSAGE);
//		try {
//			 String command;
//            if (System.getProperty("os.name").toLowerCase().contains("win")) {
//                command = vagrantCommand + " /c cd " + vagrantDir + "\\" + usuario + " && " + vagrantCommand + " destroy -f";
//            } else {
//                command = vagrantCommand + " cd " + vagrantDir + "/" + usuario + " && " + vagrantCommand + " destroy -f";
//            }
//			pb = new ProcessBuilder(command);
//			pr = pb.start();
//			doDelete();
//		} catch (IOException e) {
//			System.out.println("entrou no catch1 ");
//			e.printStackTrace();
//		}
//	}
//}