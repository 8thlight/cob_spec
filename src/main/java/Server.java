import java.io.IOException;
import java.net.Socket;

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
        while (!hostAvailable()) {
          Thread.sleep(2000);
        }
    }

    public boolean hostAvailable() {
        Socket s = null;
        try {
            s = new Socket("0.0.0.0", Integer.parseInt(port));
            return true;
        } catch (IOException ex) {
            /* ignore */
        } finally {
            try {
                if (s != null) s.close();
            } catch (IOException ex) {
                /* ignore */
            }
        }
        return false;
    }

    public void stopServer() throws Exception {
        process.destroy();
    }
}
