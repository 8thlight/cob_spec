package util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import java.io.IOException;

public class Http {

    private final String host;
    private final String port;

    public Http(String host, String port) {
        this.host = host;
        this.port = port;
    }

    public HttpResponse get(String url) throws IOException {
        return makeStandardRequest(new HttpGet(fullUrlFrom(url)));
    }

    public HttpResponse makeStandardRequest(HttpRequestBase request) throws IOException {
        HttpClient client = HttpClientBuilder
                .create()
                .disableRedirectHandling()
                .build();
        return executeRequest(client, request);
    }

    public HttpResponse executeRequest(HttpClient client, HttpRequestBase request) throws IOException {
        return client.execute(request);
    }


    private String fullUrlFrom(String url) {
        return "http://" + host + ":" + port + url;
    }
}
