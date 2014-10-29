public class Server {
  private String startCommand;
  private String directory;
  private String port;
  private static int pid;
  private Process process;

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
    process = Runtime.getRuntime().exec(startCommand + " -p " + port + " -d " + directory);
    Thread.sleep(2);
  }

  public void stopServer() throws Exception {
    process.destroy();
  }
}
