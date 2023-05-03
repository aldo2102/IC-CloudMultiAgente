package agenteVagrant;

import static agenteVagrant.GlobalVars.*;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

public class AgenteIniciar extends Agent {
	String r0, ler;
	ProcessBuilder pb;
	Process pr;
	BufferedReader reader;
	String line;

	@Override
	protected void setup() {

		System.out.println("Login Agente iniciar-ONline");

		principal.controle++;
		String usuario = principal.controle + "";

		// Execute no Windows, cmd, / c = terminate ap�s a execu��o
		// processBuilder.command("cmd.exe", "/c","cd
		// C:\\HashiCorp\\Vagrant\\"+usuario);

		try {

			try {
				String[] commandi = { cmdDotExe, vagrant2, "cd " + "C:\\HashiCorp\\Vagrant" + " && mkdir " + usuario };

				pb = new ProcessBuilder(commandi);
				pr = pb.start();

				String[] commande = { cmdDotExe, vagrant2,
						"cd " + "C:\\HashiCorp\\Vagrant\\" + usuario + "&& vagrant init" };

				pb = new ProcessBuilder(commande);
				pr = pb.start();
				pr.waitFor();

				FileWriter myWriter = new FileWriter("C:\\HashiCorp\\Vagrant\\" + usuario + "\\Vagrantfile");
				myWriter.write("Vagrant.configure(\"2\") do |config|\r\n"
						+ "  config.vm.box = \"" + box + "\"\r\n"
						+ "  config.vm.box_version = \"1\"\r\n"

						+ "  config.vm.provider 'virtualbox' do |vb|\r\n"
						+ "vb.memory = " + 512 + "\r\n"
						+ "vb.cpus =" + 2 + "\r\n"
						+ "   end\r\n"
						+ "end");
				myWriter.close();
				System.out.println("Successfully wrote to the file.");
			} catch (IOException e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			}

			// mkdir usuario
			// Process process = processBuilder.start();

			System.out
					.println("cd C:\\HashiCorp\\Vagrant\\" + usuario + " && " + vagrantDotExePath + " box add " + box);
			String[] command1 = { cmdDotExe, vagrant2,
					"cd " + "C:\\HashiCorp\\Vagrant\\" + usuario + " && " + vagrantDotExePath + " box add " + box };

			System.out.println("cd " + "C:\\HashiCorp\\Vagrant" + usuario + " && " + vagrantDotExePath + " up");
			String[] command2 = { cmdDotExe, vagrant2, "cd " + "C:\\HashiCorp\\Vagrant\\" + usuario + " && " + up };
			pr.waitFor();

			pb = new ProcessBuilder(command1);
			pr = pb.start();
			pb = new ProcessBuilder(command2);
			pr = pb.start();
			// est� puland o while
			System.out.println("teste123");
			pr.waitFor();
			System.out.println(pr);

			reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			while (pr.waitFor() == 0 && (line = reader.readLine()) != null) {
				System.out.println("entrou");
				System.out.println(line);
				System.out.println(pr.waitFor());
			}

			System.out.println("create MV(program encerrou)" + reader);

		}

		catch (IOException e) {
			System.out.println("entrou no catch1 ");
			e.printStackTrace();
		} catch (InterruptedException e2) {
			System.out.println("entrou no catch2 ");
			e2.printStackTrace();
		}

		ACLMessage message = new ACLMessage(ACLMessage.INFORM);
		message.addReceiver(new AID("AgenteMonitor", AID.ISLOCALNAME));
		message.setContent("Login Agente Monitor-Online");
		this.send(message);

		doDelete();

		// Printout a welcome message

	}

}