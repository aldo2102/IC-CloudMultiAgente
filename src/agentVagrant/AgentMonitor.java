package agentVagrant;

import jade.core.Agent;
import jade.core.Runtime;
import jade.core.ContainerID;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

import static agentVagrant.GlobalVars.*;

public class AgentMonitor extends Agent {

    private String osCommand;
    private String cmdPath;
    private String vagrantMachinePath;
    private String up;

    // Método para inicializar as configurações de monitoramento (substitua os valores corretos)
    public void setupMonitor(String osCommand, String cmdPath, String vagrantMachinePath, String up) {
        this.osCommand = osCommand;
        this.cmdPath = cmdPath;
        this.vagrantMachinePath = vagrantMachinePath;
        this.up = up;
    }

    // Método para coletar as informações de CPU e memória de uma máquina específica
    private void collectMachineUsage() throws IOException, InterruptedException {

        // Get the online VM's id's


        // Turn on Virtual Machine
//        String[] vagMachinePath = {osCommand, cmdPath, "cd " + vagrantMachinePath, "&& " + up};
//        ProcessBuilder pb = new ProcessBuilder(vagMachinePath);
//        Process pr = pb.start();
//        pr.waitFor();

        // Getting CPU usage data
        String[] dstatData = {osCommand, cmdPath, "cd " + vagrantMachinePath, " && vagrant ssh -c 'top -b -n 1'"};
        pb = new ProcessBuilder(dstatData);
        pr = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
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

        // Print the results
        System.out.println("CPU Used: " + cpuUsed);
        System.out.println("CPU Free: " + cpuFree);
        System.out.println("Memory Used: " + memUsed);
        System.out.println("Memory Free: " + memFree);
    }

    // Método para coletar informações de todas as máquinas online
    private void collectAllMachinesUsage() throws IOException, InterruptedException {
//        // Obter referência para o ContainerController
//        Runtime runtime = Runtime.instance();
//        jade.core.Profile profile = new jade.core.ProfileImpl();
//        jade.wrapper.AgentContainer mainContainer = runtime.createMainContainer(profile);
//
//        // Obter uma lista de todos os nomes de agentes/máquinas no container
//        String agentName = mainContainer.getName();
//        // Iterar sobre cada agente/máquina e coletar as informações de uso
//        ContainerID machineID = new ContainerID(agentName, null);
//        collectMachineUsage(machineID);
    }

    @Override
    protected void setup() {
        // Chame o método setupMonitor com as configurações apropriadas

        // Disk root dir disk
        File rootDir = new File("/");
        String rootPath = rootDir.getAbsolutePath();
        String vagrantPath = Paths.get(rootPath, "Hashicorp/Vagrant").toString();

        setupMonitor(GlobalVars.osCommand, GlobalVars.getCmdPath(), vagrantPath, GlobalVars.up);

        // Chame o método collectAllMachinesUsage para coletar e imprimir as informações de todas as máquinas online
        try {
            collectAllMachinesUsage();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Finalize o agente após a coleta das informações (opcional)
        endAgent();
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
