package agenteVagrant;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.core.Agent;

import java.io.PrintWriter;
import javax.swing.JOptionPane;

public class AgenteDestroy extends Agent {
	String r0, ler;

	String vagrant2 = "/c";
	String vagrant3 = "cmd.exe";
	String vagrant = "C:\\HashiCorp\\Vagrant\\bin\\vagrant.exe";

	BufferedReader reader;
	String line;
	String init = "vagrant init";
	String up = "vagrant up --provider virtualbox";
	// String box="ubuntu/bionic64";
	String box = "aldohenrique/mase";
	String status = "'dstat -cmdn' ";
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
		String a;

		ProcessBuilder processBuilder = new ProcessBuilder();
		// Scanner ler=new Scanner(System.in);
		// usuario=ler.next();

		try {
			String[] commandd = { vagrant3, vagrant2,
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