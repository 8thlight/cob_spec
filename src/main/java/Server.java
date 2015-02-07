public class Server {
    private String startCommand;
    private String directory;
    private String port;
    static private Process process;

    public void setStartCommand(String command) {
        startCommand = command;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void startServer() throws Exception {
        String command = startCommand + " -p " + port + " -d " + directory;
        process = Runtime.getRuntime().exec(command);
        Thread.sleep(2000);
    }

    public void stopServer() throws Exception {
        process.destroy();
    }
}
