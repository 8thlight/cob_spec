import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalTime;

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
    }

    public boolean serverIsStartedWithin(String timeout) {
        LocalTime abortTime = LocalTime.now().plus(Duration.parse(timeout));
        while (LocalTime.now().isBefore(abortTime)) {
            if (isServerListeningOn(Integer.parseInt(this.port)))
                return true;
        }
        return false;
    }

    private boolean isServerListeningOn(int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("localhost", port), 1000);
            return true;
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException(e);
        } catch (IOException e) {
            return false;
        }
    }

    public void stopServer() {
        process.destroy();
    }
}
