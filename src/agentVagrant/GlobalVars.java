package agentVagrant;

public class GlobalVars {
    public static String vagrantCommand;
    public static String up;
    public static String box;
	public static String vagrantDir;
    

    static {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            vagrantCommand = "cmd.exe";
            up = "vagrant up";
            box = "aldohenrique/mase";
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("mac")) {
            vagrantCommand = "/bin/bash";
            up = "vagrant up";
            box = "aldohenrique/mase";
        } else {
            throw new UnsupportedOperationException("Sistema operacional não suportado.");
        }
    }
    public static String status = "'dstat -cmdn' ";
    public static String vagrant4 = "explorer.exe /separate /c";
    public static int control = 0;
    public static int CREATE_AGENT = 1;
    public static int DESTOY_AGENT = 2;
    public static int EXIT_SYSTEM = 3;
}
