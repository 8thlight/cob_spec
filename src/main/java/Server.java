import java.io.IOException;
import java.net.Socket;

public class Server {
    private String startCommand;
    private String directory;
    private String host;
    private String port;
    static private Process process;

    public void setStartCommand(String command) {
        startCommand = command;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public void setHost(String host) {
      this.host = host;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void startServer() throws Exception {
        String command = startCommand + " -p " + port + " -d " + directory;
        process = Runtime.getRuntime().exec(command);
        while (!serverAvailable()) {
          Thread.sleep(2000);
        }
    }

    public boolean serverAvailable() {
        Socket socket = null;
        try {
            socket = new Socket(host, Integer.parseInt(port));
            return true;
        } catch (IOException ex) {
            /* ignore */
        } finally {
          closeSocket(socket);
        }
        return false;
    }

    private void closeSocket(Socket socket) {
        try {
          if (socket != null) {
              socket.close();
          }
        } catch (IOException ex) {
            /* ignore */
        }
    }

    public void stopServer() throws Exception {
        process.destroy();
    }
}
