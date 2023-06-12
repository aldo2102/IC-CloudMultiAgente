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
	Process pr;
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
			 String[] command4;
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                command4 = new String[]{"cmd.exe", "/c",
                        "cd " + GlobalVars.vagrantDir + " && " + GlobalVars.vagrantCommand + " ssh -c 'sudo apt-get install dstat'"};
            } else {
                command4 = new String[]{GlobalVars.vagrantCommand, "cd", GlobalVars.vagrantDir, "&&", GlobalVars.vagrantCommand, "ssh -c 'sudo apt-get install dstat'"};
            }

			processBuilder = new ProcessBuilder(command4);
			Process pr = processBuilder.start();
			//pr.waitFor();
			reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			while (pr.waitFor() == 0 && (line = reader.readLine()) != null) {
				System.out.println("Entrou no loop");
				System.out.println(line);
			}

			System.out.println("Dstat Instalado");

			Runtime runtime = Runtime.getRuntime();
			 if (System.getProperty("os.name").toLowerCase().contains("win")) {
                runtime.exec("cmd.exe /c cd " + GlobalVars.vagrantDir + " && start cmd.exe /k \"" + GlobalVars.vagrantCommand + " ssh\"");
            } else {
                runtime.exec(GlobalVars.vagrantCommand + " cd " + GlobalVars.vagrantDir + " && " + GlobalVars.vagrantCommand + " ssh");
            }
            //pr.waitFor();

			String[] command;
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                command = new String[]{"cmd.exe", "/c",
                        "cd " + GlobalVars.vagrantDir + " && " + GlobalVars.vagrantCommand + " ssh -c 'dstat -cmdn '"};
            } else {
                command = new String[]{GlobalVars.vagrantCommand, "cd", GlobalVars.vagrantDir, "&&", GlobalVars.vagrantCommand, "ssh -c 'dstat -cmdn'"};
            }

    		processBuilder = new ProcessBuilder(command);

			pr = processBuilder.start();
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

				FileWriter arq = new FileWriter(GlobalVars.vagrantDir + "/MediaCpuMem" + usuario + ".txt");
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