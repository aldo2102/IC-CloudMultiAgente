package agentVagrant;

import jade.core.Agent;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static agentVagrant.GlobalVars.*;

public class AgentStart extends Agent {
    ProcessBuilder pb;
    Process pr;
    BufferedReader reader;

    @Override
    protected void setup() {
        System.out.println("----AgentStart Starting----");

        try {
            // Generate an ID with a random UUID + the agent name
            UUID uuid = UUID.randomUUID();
            String MachineName = sanitizeFileName(uuid + getAID().getName());

            // Disk root dir disk
            File rootDir = new File("/");
            String rootPath = rootDir.getAbsolutePath();
            String hashicorpPath = Paths.get(rootPath, "Hashicorp").toString();
            Path hashicorpPathDirectory = Paths.get(hashicorpPath);
            System.out.println("Hashicorp dir: " + hashicorpPathDirectory);

            // Verify if vagrant dir already exists before creating it
            if (!Files.exists(hashicorpPathDirectory)) {
                System.out.println("Hashicorp dir does not exist or not found.");
                try {
                    Files.createDirectories(hashicorpPathDirectory);
                    System.out.println("Dir created successfully!");
                } catch (IOException e) {
                    System.out.println("Error creating machine dir: " + e.getMessage());
                }
                // TODO: Substituir essa forma de criar o diretório do hashicorp, caso não exista, o úsuario pode colocar manualmente onde está ou ele deve instalar o vagrant e instalar novamente o IC-CloudMultiAgente
            }

            // Vagrant Virtual Machine Path '+ "_" + machineId'
            String vagrantMachinePath = Paths.get(hashicorpPath, MachineName ).toString();
            Path virtualMachinesPath = Paths.get(vagrantMachinePath);

            // Verify if the Virtual Machine dir already exists before creating
            if (!Files.exists(virtualMachinesPath)) {
                try {
                    System.out.println("Virtual machine dir does not exist: " + virtualMachinesPath);
                    Files.createDirectories(virtualMachinesPath);
                    System.out.println("Virtual machine dir created successfully on: " + virtualMachinesPath);
                } catch (IOException e) {
                    System.out.println("Error creating machine dir: " + e.getMessage());
                }
            } else {
                System.out.println("Dir already exists");
                endAgent();
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
                    System.out.println("Machine config successfully wrote to the Vagrantfile file on the virtual machine dir: " + virtualMachinesPath);
                }
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

            // Crate Machine
            String[] createMachine = {osCommand, cmdPath, "cd " + vagrantMachinePath, "&& vagrant init"};
            System.out.println("Creating machine: " + Arrays.toString(createMachine));
            pb = new ProcessBuilder(createMachine);
            pr = pb.start();
            pr.waitFor();

            // Create box
            String[] createBox = {osCommand, cmdPath, "cd " + vagrantMachinePath, "&& " + "C:/HashiCorp/Vagrant/bin/vagrant.exe" + " box add " + box};
            System.out.println("Creating box: " + Arrays.toString(createBox));
            pb = new ProcessBuilder(createBox);
            pr = pb.start();
            pr.waitFor();

            // Turn on Virtual Machine
            String[] turnMachineOn = {osCommand, cmdPath, "cd " + vagrantMachinePath, "&& " + up};
            System.out.println("Turning on machine: " + Arrays.toString(turnMachineOn));
            pb = new ProcessBuilder(turnMachineOn);
            pr = pb.start();
            pr.waitFor();

            // Getting CPU usage data
            String[] dstatData = {osCommand, cmdPath, "cd " + vagrantMachinePath, " && vagrant ssh -c 'top -b -n 1'"};
            System.out.println("Getting CPU usage data: " + Arrays.toString(dstatData));
            pb = new ProcessBuilder(dstatData);
            pr = pb.start();

            reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line;

            // Skip the rows until get to the row with the CPU data
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("%Cpu(s)")) {
                    break;
                }
            }

            // Extract the CPU usage from the found row
            float cpuUsed = 0;
            float cpuFree = 0;
            if (line != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length >= 9) {
                    cpuUsed = Float.parseFloat(parts[1]);
                    cpuFree = Float.parseFloat(parts[3]);
                } else {
                    System.out.println("Insufficient data on the line");
                }
            } else {
                System.out.println("Line not found");
            }

            pr.waitFor();

            // Getting Memory usage data
            dstatData = new String[]{osCommand, cmdPath, "cd " + vagrantMachinePath, " && vagrant ssh -c 'free -m'"};
            System.out.println("Getting memory data: " + Arrays.toString(dstatData));
            pb = new ProcessBuilder(dstatData);
            pr = pb.start();

            reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));

            // Skip the rows until get to the row with the Memory data
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Mem:")) {
                    break;
                }
            }

            // Extract the Memory usage from the found row
            float memUsed = 0;
            float memFree = 0;
            if (line != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length >= 7) {
                    memUsed = Float.parseFloat(parts[2]);
                    memFree = Float.parseFloat(parts[3]);
                } else {
                    System.out.println("Insufficient data on the line");
                }
            } else {
                System.out.println("Line not found");
            }

            pr.waitFor();

            // Store the information in the usage_data.txt file
            System.out.println("Writing data in usage_data.txt file");
            String filePath = Paths.get(vagrantMachinePath, "usage_data.txt").toString();
            FileWriter fileWriter = new FileWriter(filePath);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println("CPU Used: " + cpuUsed);
            printWriter.println("CPU Free: " + cpuFree);
            printWriter.println("Memory Used: " + memUsed);
            printWriter.println("Memory Free: " + memFree);
            printWriter.close();

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
        endAgent();
    }

    public static String sanitizeFileName(String name) {
        if (name.contains(":")) {
            name = name.replace(":", "_");
        }
        if (name.contains("-")) {
            name = name.replace("-", "_");
        }
        if (name.contains("@")) {
            int index = name.indexOf("@");
            name = name.substring(0, index);
        }
        return name;
    }

    public void endAgent() {
        System.out.println("----AgentStart Ended..----");
        CountDownLatch latch;

        Object[] args = getArguments();

        if (args != null && args.length > 0 && args[0] instanceof CountDownLatch) {
            latch = (CountDownLatch) args[0];
        } else {
            System.err.println("Invalid reference to the CountDownLatch");
            doDelete();
            return;
        }

        latch.countDown();
    }
}


