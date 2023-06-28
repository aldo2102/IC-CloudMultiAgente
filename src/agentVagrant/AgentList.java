package agentVagrant;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import jade.core.Agent;

public class AgentList extends Agent {
	private String osCommand;
    private String cmdPath;

    @Override
    protected void setup() {
        System.out.println("----AgentList Starting----");

        try {
            // list existing virtual machines
        	osCommand = GlobalVars.getOsCommand();
            cmdPath = GlobalVars.getCmdPath();

            String[] listMachines = {osCommand, cmdPath, "vagrant", "global-status"};
            System.out.println("Listing machines: " + Arrays.toString(listMachines));
            ProcessBuilder pb = new ProcessBuilder(listMachines);
            Process pr = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line;

            // print result on output
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
        doDelete();
    }

   
}
