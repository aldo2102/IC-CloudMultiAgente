package agentVagrant;

import static agentVagrant.GlobalVars.*;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import jade.core.Agent;

public class AgentMonitor extends Agent {

	String r0, ler;
	ProcessBuilder processBuilder;
	Process startProcess;
	BufferedReader reader;
	String line;

	@Override
	protected void setup() {

		String usuario = GlobalVars.control + "";
		jade.lang.acl.ACLMessage TM0 = blockingReceive();
		System.out.println(TM0.getContent());
		ProcessBuilder processBuilder = new ProcessBuilder();
		System.out.println("---- Inicio do Agente Monitor ----");
		processBuilder.command("cmd.exe", "/c", "vagrant status");

		try {
			// command4 não está sendo executado
			String[] command4 = { cmdDotExe, vagrant4,
					"cd " + "C:\\HashiCorp\\Vagrant\\" + usuario + " && vagrant ssh -c 'sudo apt-get install dstat'" };

			processBuilder = new ProcessBuilder(command4);
			Process startProcess = processBuilder.start();
			startProcess.waitFor();
			reader = new BufferedReader(new InputStreamReader(startProcess.getInputStream()));
			while (startProcess.waitFor() == 0 && (line = reader.readLine()) != null) {
				System.out.println("Entrou no loop");
				System.out.println(line);
			}

			System.out.println("Dstat Instalado");

			Runtime runtime = Runtime.getRuntime();
			runtime.exec("cmd.exe /c cd C:\\HashiCorp\\Vagrant\\" + usuario + " && start cmd.exe /k \"vagrant ssh\"");
			startProcess.waitFor();

			String[] command = { cmdDotExe, vagrant4,
					"cd " + "C:\\HashiCorp\\Vagrant\\" + usuario + " && vagrant ssh -c 'dstat -cmdn '" };

			processBuilder = new ProcessBuilder(command);

			startProcess = processBuilder.start();
			reader = new BufferedReader(new InputStreamReader(startProcess.getInputStream()));

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
				line = line.trim().replaceAll("0;0", "");
				line = line.trim().replaceAll("\\s+", " ");
				line = line.trim().replaceAll("\\|+", " ");
				line = line.trim().replaceAll("[a-zA-Z]+", "");
				// System.out.println("line 4 "+line);

				// line=line.trim().replaceAll(" ","P");
				String[] parts = line.split(" ");
				cpuUsed += Float.parseFloat(parts[0]);
				cpuUsed += Float.parseFloat(parts[1]);
				// cpuFree += Float.parseFloat(parts[2]);
				memUsed += Float.parseFloat(parts[7]);
				// memFree += Float.parseFloat(parts[10]);
				count++;
				mediaCpu = cpuUsed / count;
				mediaMem = memUsed / count;

				FileWriter arq = new FileWriter(
						"C:\\HashiCorp\\Vagrant\\" + usuario + "\\MediaCpuMem" + usuario + ".txt");
				PrintWriter gravarArq = new PrintWriter(arq);
				gravarArq.println("Media de CPU usada =" + mediaCpu);
				gravarArq.println("Media de Memoria usada em MegaBits =" + mediaMem);
				arq.close();
			}

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("---- Fim do Agente Monitor ----");
		doDelete();
	}
}