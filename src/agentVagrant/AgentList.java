package agentVagrant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import jade.core.Agent;

import static agentVagrant.GlobalVars.*;

public class AgentList extends Agent {

    @Override
    protected void setup() {
        System.out.println("----AgentList Starting----");

        try {
            // List existing virtual machines

            String[] listMachines = {osCommand, cmdPath, "vagrant", "global-status", "--prune"};
            System.out.println("Listing machines: " + Arrays.toString(listMachines));
            ProcessBuilder pb = new ProcessBuilder(listMachines);
            Process pr = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line;

            // Print result on output
            System.out.println("Existing machines:");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
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
}
