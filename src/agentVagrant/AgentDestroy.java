package agentVagrant;

import static agentVagrant.GlobalVars.*;

import java.io.BufferedReader;
import java.io.IOException;

import jade.core.Agent;

import javax.swing.JOptionPane;

public class AgentDestroy extends Agent {
	String r0, ler;
	BufferedReader reader;
	String line;
	ProcessBuilder pb;
	Process pr;

	@Override
	protected void setup() {
		JOptionPane.showMessageDialog(null, "PRONTO PARA DESTRUIR!", "ALERTA", JOptionPane.WARNING_MESSAGE);
		String usuario;
		usuario = JOptionPane.showInputDialog(null, "Escreva o nome da maquina que será destruída", "ALERTA",
				JOptionPane.WARNING_MESSAGE);
		try {
			 String command;
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                command = vagrantCommand + " /c cd " + vagrantDir + "\\" + usuario + " && " + vagrantCommand + " destroy -f";
            } else {
                command = vagrantCommand + " cd " + vagrantDir + "/" + usuario + " && " + vagrantCommand + " destroy -f";
            }
			pb = new ProcessBuilder(command);
			pr = pb.start();
			doDelete();
		} catch (IOException e) {
			System.out.println("entrou no catch1 ");
			e.printStackTrace();
		}
	}
}