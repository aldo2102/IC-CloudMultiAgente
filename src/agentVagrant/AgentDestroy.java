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
			String[] commandd = { cmdDotExe, vagrant2,
					"cd " + "C:\\HashiCorp\\Vagrant\\" + usuario + " && vagrant destroy -f" };
			pb = new ProcessBuilder(commandd);
			pr = pb.start();
			doDelete();
		} catch (IOException e) {
			System.out.println("entrou no catch1 ");
			e.printStackTrace();
		}
	}
}