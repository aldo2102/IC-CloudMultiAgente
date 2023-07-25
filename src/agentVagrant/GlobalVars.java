package agentVagrant;

public class GlobalVars {
    public static String osCommand;
    public static String up;
    public static String box;
    public static String cmdPath;
    
    public static String getOsCommand() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "cmd";
        } else {
            return "bash";
        }
    }

    public static String getCmdPath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "/c";
        } else {
            return "-c";
        }
    }

    static {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            cmdPath = "/c";
            osCommand = "cmd.exe";
            up = "vagrant up";
            box = "aldohenrique/mase";
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("mac")) {
            cmdPath = "-c";
            osCommand = "/bin/bash";
            up = "vagrant up";
            box = "aldohenrique/mase";
        } else {
            throw new UnsupportedOperationException("Operating system not supported");
        }
    }

    public static final int EXIT_SYSTEM = 0;
    public static final int CREATE_MACHINES = 1;
    public static final int LIST_MACHINES = 2;
    public static final int MONITOR_MACHINES = 3;
    public static final int DESTROY_MACHINES = 4;

    public static int MEMORY = 512;
    public static int CPUS = 2;
}
