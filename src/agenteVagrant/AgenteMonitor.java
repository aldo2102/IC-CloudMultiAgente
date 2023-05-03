package agenteVagrant;

import static agenteVagrant.GlobalVars.*;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import jade.core.Agent;

public class AgenteMonitor extends Agent {

	String r0, ler;
	ProcessBuilder pb;
	Process pr;
	BufferedReader reader;
	String line;

	@Override
	protected void setup() {

		String usuario = principal.controle + "";
		ProcessBuilder pb;
		Process pr;
		System.out.println("cheguis1");

		jade.lang.acl.ACLMessage TM0 = blockingReceive();
		System.out.println(TM0.getContent());

		System.out.println("cheguis2");

		ProcessBuilder processBuilder = new ProcessBuilder();

		// Execute no Windows, cmd, / c = terminate após a execução
		processBuilder.command("cmd.exe", "/c", "vagrant status");

		try {

			String[] command4 = { cmdDotExe, vagrant4,
					"cd " + "C:\\HashiCorp\\Vagrant\\" + usuario + " && vagrant ssh -c 'sudo apt-get install dstat'" };

			pb = new ProcessBuilder(command4);
			pr = pb.start();

			pr.waitFor();
			reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			while (pr.waitFor() == 0 && (line = reader.readLine()) != null) {
				System.out.println("entrou 2");
				System.out.println(line);
				// System.out.println(pr.waitFor());
			}

			System.out.println("Dstat Instalado");

			Runtime rt = Runtime.getRuntime();
			rt.exec("cmd.exe /c cd C:\\HashiCorp\\Vagrant\\" + usuario + " && start cmd.exe /k \"vagrant ssh\"");
			pr.waitFor();

			String[] command = { cmdDotExe, vagrant4,
					"cd " + "C:\\HashiCorp\\Vagrant\\" + usuario + " && vagrant ssh -c 'dstat -cmdn '" };

			pb = new ProcessBuilder(command);

			pr = pb.start();

			// pr.waitFor();

			// String media;
			reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));

			line = "";
			int count = 0;
			// String cpu = "";
			// float cpuFree = 0;
			float cpuUsed = 0;
			// float memFree = 0;
			float memUsed = 0;
			float mediaCpu = 0;
			float mediaMem = 0;

			while ((line = reader.readLine()) != null) {
				// System.out.println("entrou 3");
				// System.out.println(line);

				line = line.trim().replaceAll("0;0", "");

				line = line.trim().replaceAll("\\s+", " ");

				line = line.trim().replaceAll("\\|+", " ");

				line = line.trim().replaceAll("[a-zA-Z]+", "");
				// System.out.println("line 4 "+line);

				// line=line.trim().replaceAll(" ","P");
				String[] parts = line.split(" ");
				try {
					cpuUsed += Float.parseFloat(parts[0]);
					cpuUsed += Float.parseFloat(parts[1]);
					// cpuFree += Float.parseFloat(parts[2]);
					memUsed += Float.parseFloat(parts[7]);
					// memFree += Float.parseFloat(parts[10]);
					count++;

					mediaCpu = cpuUsed / count;
					mediaMem = memUsed / count;

					// System.out.println(parts[2]);
				} catch (Exception e) {
					// System.out.println(e.getMessage() + " tam=" + parts.length + "'" + line +
					// "'");
				}

				/*
				 * System.out.println("CPU USADO ="+cpuUsed);
				 * System.out.println("CPU LIVRE ="+cpuFree);
				 * System.out.println("memoria usada ="+memUsed);
				 * System.out.println("memoria LIVRE ="+memFree);
				 */

				//

				// float soma =Float.parseFloat(line);
				// System.out.println("teste"+soma);
				// soma=soma+soma;

				// cpu=line.trim().substring(0,1);
				// System.out.println("line 1"+cpu);

				// System.out.println("Digite 's' para destruir a maquina, enquanto ela está
				// executando");

				// Scanner ler =new Scanner (System.in);
				// r0=ler.nextLine();

				/*
				 * if(r0.equals("S") || r0.equals("s")) {
				 * continue;
				 * 
				 * 
				 * }else {
				 */
				// System.out.println("Media de CPU usada ="+mediaCpu);
				// System.out.println("Media de Memoria usada em MegaBits ="+mediaMem);

				FileWriter arq = new FileWriter(
						"C:\\HashiCorp\\Vagrant\\" + usuario + "\\MediaCpuMem" + usuario + ".txt");
				PrintWriter gravarArq = new PrintWriter(arq);

				gravarArq.println("Media de CPU usada =" + mediaCpu);

				gravarArq.println("Media de Memoria usada em MegaBits =" + mediaMem);

				arq.close();

				// System.out.println("Salvo em
				// C:\\\\Users\\\\vinic\\\\Documents\\\\MediaCpuMem.txt");

				// break;

				// }

				/*
				 * ACLMessage message = new ACLMessage(ACLMessage.INFORM);
				 * message.addReceiver(new AID("AgenteDestroy",AID.ISLOCALNAME));
				 * message.setContent("Teste Destruindo");
				 * this.send(message);
				 */

			}

			/*
			 * System.out.print("Destruindo a maquina");
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * String[] commandd = {vagrant3, vagrant2,"cd " + "C:\\HashiCorp\\Vagrant"+
			 * " && vagrant destroy -f"};
			 * 
			 * 
			 * pb = new ProcessBuilder(commandd);
			 * pr = pb.start();
			 */

		} catch (IOException e) {
			System.out.println("entrou no catch1 ");
			e.printStackTrace();
		} catch (InterruptedException e2) {
			System.out.println("entrou no catch2 ");
			e2.printStackTrace();

		}
		System.out.println("entrou no ");
		doDelete();

		System.out.println("entrou no ");
		doDelete();

	}
}