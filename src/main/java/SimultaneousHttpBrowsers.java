import org.apache.http.HttpResponse;
import util.Http;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class SimultaneousHttpBrowsers {

    private String port;
    private String host;
    private int requestCount;
    List<Callable<HttpResponse>> requests;
    private long duration;
    List<Future<HttpResponse>> responses;

    public void setPort(String port) {
        this.port = port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void addRequests(final String url, int requestCount) {
        requests = new ArrayList<Callable<HttpResponse>>();
        this.requestCount = requestCount;
        for (int i = 0; i < requestCount; i++) {
            requests.add(new Callable<HttpResponse>() {
                             @Override
                             public HttpResponse call() throws Exception {
                                 Http browser = new Http(host, port);
                                 return browser.get(url);
                             }
                         }
            );
        }
    }

    public void execute() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(requestCount);
        long startTime = System.nanoTime();
        responses = service.invokeAll(requests);
        service.shutdown();
        long endTime = System.nanoTime();
        duration = (endTime - startTime);
    }

    public String time() {
        long seconds = (duration / 1000000000);
        return String.format("%d seconds", seconds);
    }

    public boolean allResponseCodesEqual(int code) throws ExecutionException, InterruptedException {
        boolean allTrue = true;
        for (Future<HttpResponse> response : responses) {
            allTrue = allTrue && response.get().getStatusLine().getStatusCode() == code;
        }

        return allTrue;
    }
}
