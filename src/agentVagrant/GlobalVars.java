package agentVagrant;

public class GlobalVars {
    public static String osCommand;
    public static String up;
    public static String box;
    public static String cmdPath;
    

    static {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            cmdPath = "/c";
            osCommand = "cmd.exe";
            up = "vagrant up";
            box = "aldohenrique/mase";
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("mac")) {
//            cmdPath = "/c";
            osCommand = "/bin/bash";
            up = "vagrant up";
            box = "aldohenrique/mase";
        } else {
            throw new UnsupportedOperationException("Operating system not supported");
        }
    }
    public static String vagrant4 = "explorer.exe /separate /c";

    public static int LIST_MACHINES = 0;
    public static int CREATE_MACHINES = 1;
    public static int DESTROY_MACHINES = 2;
    public static int MONITOR_MACHINES = 3;

    public static int MEMORY = 512;
    public static int CPUS = 2;
}
