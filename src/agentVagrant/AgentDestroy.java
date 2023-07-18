package agentVagrant;

import jade.core.Agent;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

import static agentVagrant.GlobalVars.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class AgentDestroy extends Agent {

    @Override
    protected void setup() {
        System.out.println("----AgentDestroy Starting----");

        // Get a reference to the agent container
        AgentContainer container = getContainerController();

        try {
            CountDownLatch latch = new CountDownLatch(1);
            Object[] agentArgs = new Object[]{latch};
            AgentController agentList = container.createNewAgent(
                    "AgentList" + System.currentTimeMillis(),
                    "agentVagrant.AgentList",
                    agentArgs);
            agentList.start();
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Prompt user to enter the machine name to destroy
        String machineName = getUserInput();

        // Destroy the selected machine
        destroyMachine(machineName);

        System.out.println("----AgentDestroy Ended----");

        Object[] args = getArguments();

        CountDownLatch latch;

        if (args != null && args.length > 0 && args[0] instanceof CountDownLatch) {
            latch = (CountDownLatch) args[0];
        } else {
            System.err.println("Invalid reference to the CountDownLatch");
            doDelete();
            return;
        }

        doDelete();

        latch.countDown();
    }

    private String getUserInput() {
        System.out.print("Enter the id of the machine to be destroyed: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void destroyMachine(String machineName) {

        try {
            // Run the 'vagrant destroy' command to destroy the specified machine
            String[] destroyMachineCommand = {osCommand, cmdPath, "vagrant", "destroy", machineName, "--force"};
            System.out.println("Destroying machine: " + Arrays.toString(destroyMachineCommand));
            ProcessBuilder pb = new ProcessBuilder(destroyMachineCommand);
            Process pr = pb.start();
            pr.waitFor();

            System.out.println("Machine " + machineName + " destroyed successfully.");
        } catch (IOException | InterruptedException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}