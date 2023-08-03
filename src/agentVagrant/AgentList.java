package agentVagrant;

import jade.core.Agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static agentVagrant.GlobalVars.*;

public class AgentList extends Agent {

    private List<String> machineList;

    @Override
    protected void setup() {
        System.out.println("----AgentList Starting----");

        try {
            machineList = new ArrayList<>();

            // List existing virtual machines
            String[] listMachines = {osCommand, cmdPath, "vagrant", "global-status", "--prune"};
            System.out.println("Listing machines: " + Arrays.toString(listMachines));
            ProcessBuilder pb = new ProcessBuilder(listMachines);
            Process pr = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line;

            // Add machine names to the list
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length >= 3) {
                    machineList.add(parts[1]);
                }
            }

            pr.waitFor();
        } catch (IOException | InterruptedException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        System.out.println("----AgentList Ended----");

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

    public List<String> getMachineList() {
        return machineList;
    }
}
