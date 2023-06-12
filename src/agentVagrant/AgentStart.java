package agentVagrant;

import static agentVagrant.GlobalVars.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.lang.System;


import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

public class AgentStart extends Agent {
    String r0, ler;
    ProcessBuilder pb;
    Process pr;
    BufferedReader reader;
    String line;

    @Override
    protected void setup() {
        System.out.println("---- Inicio do Agente Iniciar ----");
        Main.controller++;
        String usuario = Main.controller + "";
        try {
            // Obter o diretório de trabalho atual e armazenar em workingDir
            String workingDir = System.getProperty("user.dir");
            
            //cria um caminho e armazena no objeto PATH
            Path vagrantDirPath = Paths.get(workingDir, "HashiCorp", "Vagrant", usuario);
            String vagrantDir = vagrantDirPath.toString();//converte o objeto PATH em uma string representando o caminho completo do diretorio
            String vagrantFilePath = Paths.get(vagrantDir, "Vagrantfile").toString();

            // Verificar se o diretório Vagrant existe e criar se necessário
            File vagrantDirectory = new File(vagrantDir);
            if (!vagrantDirectory.exists()) {
                vagrantDirectory.mkdirs(); // Cria diretórios aninhados se necessário
            }
            
            
            //verifica o sistema operacional
            //String osName = System.getProperty("os.name").toLowerCase();
            String vagrantCommand = GlobalVars.vagrantCommand;
            String box = GlobalVars.box;

            String[] commandi = { vagrantCommand, "cd", vagrantDir, "&&", "mkdir", usuario };
            pb = new ProcessBuilder(commandi);
            pr = pb.start();
            String[] commande = { vagrantCommand, "cd", vagrantDir, "&&", "vagrant", "init" };
            pb = new ProcessBuilder(commande);
            pr = pb.start();
            //pr.waitFor();
            FileWriter myWriter = new FileWriter(vagrantFilePath);
            myWriter.write("Vagrant.configure(\"2\") do |config|\r\n"
                    + "  config.vm.box = \"" + box + "\"\r\n"
                    + "  config.vm.box_version = \"1\"\r\n"
                    + "  config.vm.provider 'virtualbox' do |vb|\r\n"
                    + "    vb.memory = " + 512 + "\r\n"
                    + "    vb.cpus = " + 2 + "\r\n"
                    + "  end\r\n"
                    + "end");
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
            System.out.println("cd " + vagrantDir + " && " + vagrantCommand + " box add " + box);
            String[] command1 = { vagrantCommand, "cd", vagrantDir, "&&", vagrantCommand, "box", "add", box };
            System.out.println("cd " + vagrantDir + " && " + vagrantCommand + " up");
            String[] command2 = { vagrantCommand, "cd", vagrantDir, "&&", vagrantCommand, GlobalVars.up };
            //pr.waitFor(); esta bloqueando o processo de cria��o da maquina
            pb = new ProcessBuilder(command1);
            pr = pb.start();
            pb = new ProcessBuilder(command2);
            pr = pb.start();
            //pr.waitFor();
            reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            while (pr.waitFor() == 0 && (line = reader.readLine()) != null) {
                System.out.println("entrou");
                System.out.println(line);
                System.out.println(pr.waitFor());
            }
            System.out.println("create MV(program encerrou)" + reader);
        } catch (IOException | InterruptedException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.addReceiver(new AID("AgentMonitor", AID.ISLOCALNAME));
        message.setContent("Login Agent Monitor-Online");
        this.send(message);
        System.out.println("---- Fim do Agente Inicio ----");
        doDelete();
    }
}
