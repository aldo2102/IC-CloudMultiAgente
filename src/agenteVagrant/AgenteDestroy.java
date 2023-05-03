package agenteVagrant;

import static agenteVagrant.GlobalVars.*;

import java.io.BufferedReader;
import java.io.IOException;

import jade.core.Agent;

import javax.swing.JOptionPane;

public class AgenteDestroy extends Agent {
	String r0, ler;
	BufferedReader reader;
	String line;
	ProcessBuilder pb;
	Process pr;

	@Override
	protected void setup() {

		/*
		 * jade.lang.acl.ACLMessage TD0 = blockingReceive();
		 * System.out.println(TD0.getContent());
		 */
		// System.out.println("Pronto para destruir");
		JOptionPane.showMessageDialog(null, "PRONTO PARA DESTRUIR!", "ALERTA", JOptionPane.WARNING_MESSAGE);

		String usuario/* = principal.controle+"" */;

		// System.out.print("Escreva o nome da maquina que ser� destru�da");
		usuario = JOptionPane.showInputDialog(null, "Escreva o nome da maquina que ser� destru�da", "ALERTA",
				JOptionPane.WARNING_MESSAGE);
		// Scanner ler=new Scanner(System.in);
		// usuario=ler.next();

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

		// TODO Auto-generated method stub
		// principal.controle--;

	}

}