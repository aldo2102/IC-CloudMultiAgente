package agentVagrant;

import jade.core.Agent;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static agentVagrant.GlobalVars.*;

public class AgentStart extends Agent {
    ProcessBuilder pb;
    Process pr;
    BufferedReader reader;
    String line;

    @Override
    protected void setup() {
        System.out.println("----AgentStart Starting----");
        Main.controller++;
        String MachineName = Integer.toString(Main.controller);

        try {
            // Diretório raiz do disco
            File rootDir = new File("/");
            String rootPath = rootDir.getAbsolutePath();
            String vagrantPath = Paths.get(rootPath, "Hashicorp").toString();
            Path vagrantDirectory = Paths.get(vagrantPath);
            System.out.println("Vagrant dir (hashiCorp): " + vagrantDirectory);

            // Verificar se o diretório Vagrant existe antes de criá-lo
            if (!Files.exists(vagrantDirectory)) {
                System.out.println("Vagrant dir does not exist");
                try {
                    Files.createDirectories(vagrantDirectory);
                    System.out.println("Dir created successfully!");
                } catch (IOException e) {
                    System.out.println("Error creating machine dir: " + e.getMessage());
                }
            }

            // Caminho da máquina Vagrant
            String vagrantMachinePath = Paths.get(vagrantPath, MachineName).toString();
            Path vagrantMachinesPath = Paths.get(vagrantMachinePath);

            if (!Files.exists(vagrantMachinesPath)) {
                try {
                    System.out.println("Machine dir does not exist: " + vagrantMachinesPath);
                    Files.createDirectories(vagrantMachinesPath);
                    System.out.println("Dir created successfully!");
                } catch (IOException e) {
                    System.out.println("Error creating machine dir: " + e.getMessage());
                }
            } else {
                System.out.println("Dir already exists");
                // TODO: Fazer retorno caso o diretório com o nome dessa máquina já exista, para não sobrescrever a configuração da máquina
            }

            String vagrantFilePath = Paths.get(vagrantMachinePath, "Vagrantfile").toString();

            try (FileWriter fileWriter = new FileWriter(vagrantFilePath)) {
                fileWriter.write("Vagrant.configure(\"2\") do |config|\r\n"
                        + "  config.vm.box = \"" + box + "\"\r\n"
                        + "  config.vm.box_version = \"1\"\r\n"
                        + "  config.vm.provider 'virtualbox' do |vb|\r\n"
                        + "    vb.memory = " + MEMORY + "\r\n"
                        + "    vb.cpus = " + CPUS + "\r\n"
                        + "  end\r\n"
                        + "end");
                if (Files.exists(Path.of(vagrantFilePath))) {
                    System.out.println("Successfully wrote to the file.");
                }
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

            System.out.println("Machine dir: " + vagrantMachinePath);

            // Criar Máquina
            String[] createMachine = {vagrantCommand, cmdPath, "cd " + vagrantMachinePath, "&& vagrant init"};
            System.out.println("Creating machine: " + Arrays.toString(createMachine));
            pb = new ProcessBuilder(createMachine);
            pr = pb.start();
            pr.waitFor();

            // Criar box
            String[] createBox = {vagrantCommand, cmdPath, "cd " + vagrantMachinePath, "&& " + "C:/HashiCorp/Vagrant/bin/vagrant.exe" + " box add " + box};
            System.out.println("Creating box: " + Arrays.toString(createBox));
            pb = new ProcessBuilder(createBox);
            pr = pb.start();
            pr.waitFor();

            // Ligar Máquina Virtual
            String[] turnMachineOn = {vagrantCommand, cmdPath, "cd " + vagrantMachinePath, "&& " + up};
            System.out.println("Turning on machine: " + Arrays.toString(turnMachineOn));
            pb = new ProcessBuilder(turnMachineOn);
            pr = pb.start();
            pr.waitFor();

            // Instalar Dstat
            String[] installDstat = {vagrantCommand, cmdPath, "cd " + vagrantMachinePath, "&& vagrant ssh -c 'sudo apt-get install dstat'"};
            System.out.println("Installing dstat: " + Arrays.toString(installDstat));
            pb = new ProcessBuilder(installDstat);
            pr = pb.start();
            pr.waitFor();

            // Pegando dados
            String[] dstatData = {vagrantCommand, cmdPath, "cd "+ vagrantMachinePath, " && vagrant ssh -c 'dstat -cmdn'"};
            System.out.println("Getting dstat data: " + Arrays.toString(dstatData));
            pb = new ProcessBuilder(dstatData);
            pr = pb.start();

            // SSH
//            String[] command6 = {vagrantCommand, cmdPath, "cd " + vagrantMachinePath, "&& start " + vagrantCommand + " /k \"" + vagrantCommand + " ssh\""};
//            System.out.println("ssh: " + Arrays.toString(command6));
//            pb = new ProcessBuilder(command6);
//            pr = pb.start();
//            pr.waitFor();

            reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            line = "";
            int count = 0;
            float cpuFree = 0;
            float cpuUsed = 0;
            float memFree = 0;
            float memUsed = 0;
            float mediaCpu = 0;
            float mediaMem = 0;

            FileWriter fileWriter = new FileWriter(Paths.get(vagrantMachinePath, "dstat.txt").toString());
            PrintWriter printWriter = new PrintWriter(fileWriter);

            while ((line = reader.readLine()) != null) {
                line = line.trim().replaceAll("0;0", "");
                line = line.trim().replaceAll("\\s+", " ");
                line = line.trim().replaceAll("\\|+", " ");
                line = line.trim().replaceAll("[a-zA-Z]+", "");
                System.out.println("Dstat line: " + line);

                line = line.trim().replaceAll(" ", "P");
                String[] parts = line.split(" ");

                if (parts.length >= 13 && !parts[0].isEmpty() && !parts[1].isEmpty() && !parts[2].isEmpty() && !parts[7].isEmpty() && !parts[10].isEmpty()) {
                    try {
                        float value0 = Float.parseFloat(parts[0]);   // usr
                        float value1 = Float.parseFloat(parts[1]);   // sys
                        float value2 = Float.parseFloat(parts[2]);   // idl
                        float value3 = Float.parseFloat(parts[3]);   // wai
                        float value4 = Float.parseFloat(parts[4]);   // hiq
                        float value5 = Float.parseFloat(parts[5]);   // siq

                        float value6 = Float.parseFloat(parts[6]);   // used
                        float value7 = Float.parseFloat(parts[7]);   // buff
                        float value8 = Float.parseFloat(parts[8]);   // cach
                        float value9 = Float.parseFloat(parts[9]);   // free

                        float value10 = Float.parseFloat(parts[10]);  // read
                        float value11 = Float.parseFloat(parts[6]);   // writ

                        float value12 = Float.parseFloat(parts[12]);   // recv
                        float value13 = Float.parseFloat(parts[13]);   // send


                        cpuUsed += value0;
                        cpuUsed += value1;
                        cpuFree += value2;

                        memUsed += value7;
                        memFree += value10;

                        count++;

                        mediaCpu = cpuUsed / count;
                        mediaMem = memUsed / count;

                        printWriter.println("Media de CPU usada = " + mediaCpu);
                        printWriter.println("Media de Memoria usada em MegaBits = " + mediaMem);
                    } catch (NumberFormatException e) {
                        //TODO Tratar o erro de formato inválido
                        System.out.println("Erro de formato inválido: " + e.getMessage());
                    }
                } else {
                    //TODO Tratar o caso em que não há dados suficientes em 'parts'
                    System.out.println("Dados insuficientes em 'parts'");
                }
            }

            pr.waitFor();
            fileWriter.close();

            reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            while (pr.waitFor() == 0 && (line = reader.readLine()) != null) {
                System.out.println(line);
                System.out.println(pr.waitFor());
            }
            System.out.println("Virtual Machine Created: " + reader);
        } catch (IOException | InterruptedException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        System.out.println("----AgentStart Ended..----");
        doDelete();
    }
}
