package util;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.net.URISyntaxException;

public class Http {
    private final String host;
    private final int port;

    public Http(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public HttpResponse get(String url) throws IOException {
        return makeStandardRequest(new HttpGet(fullUrlFrom(url)));
    }

    public HttpResponse head(String url) throws IOException {
        return makeStandardRequest(new HttpHead(fullUrlFrom(url)));
    }

    public HttpResponse put(String url, String data) throws IOException {
        HttpPut put = new HttpPut(fullUrlFrom(url));
        put.setEntity(new ByteArrayEntity(dataAsByteArray(data)));
        return makeStandardRequest(put);
    }

    public HttpResponse post(String url, String data) throws IOException {
        HttpPost post = new HttpPost(fullUrlFrom(url));
        post.setEntity(new ByteArrayEntity(dataAsByteArray(data)));
        return makeStandardRequest(post);
    }

    public HttpResponse patch(String url, String data, String eTag) throws IOException {
        HttpPatch patch = new HttpPatch(fullUrlFrom(url));
        patch.setHeader(HttpHeaders.IF_MATCH, eTag);
        patch.setEntity(new ByteArrayEntity(dataAsByteArray(data)));
        return makeStandardRequest(patch);
    }

    public HttpResponse delete(String url) throws IOException {
        return makeStandardRequest(new HttpDelete(fullUrlFrom(url)));
    }

    public HttpResponse options(String url) throws IOException {
        return makeStandardRequest(new HttpOptions(fullUrlFrom(url)));
    }

    public HttpResponse bogusRequest(String url) throws IOException, URISyntaxException {
        return makeStandardRequest(new BogusRequest(fullUrlFrom(url)));
    }

    public HttpResponse getWithPartialHeader(String url, String range) throws IOException {
        HttpClient client = HttpClients.custom().build();
        HttpUriRequest request = RequestBuilder
                .get()
                .setUri(fullUrlFrom(url))
                .setHeader(HttpHeaders.RANGE, range)
                .build();
        return client.execute(request);
    }

    public HttpResponse getWithCredentials(String url, String username, String password) throws IOException {
        HttpClient client = HttpClients.custom().build();

        String authCredentials = username + ":" + password;
        String authSchemeWithCredentials = "Basic " + base64Encode(authCredentials);

        HttpUriRequest request = RequestBuilder
                .get()
                .setUri(fullUrlFrom(url))
                .setHeader(HttpHeaders.AUTHORIZATION, authSchemeWithCredentials)
                .build();

        return client.execute(request);
    }

    public HttpResponse getWithCookie(String url, String cookie) throws IOException {
        HttpClient client = HttpClients.custom().build();
        HttpUriRequest request = RequestBuilder
                .get()
                .setUri(fullUrlFrom(url))
                .setHeader("Cookie", cookie)
                .build();
        return client.execute(request);
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

    private byte[] dataAsByteArray(String data) {
        return (data != null) ? data.getBytes() : "".getBytes();
    }

    private String base64Encode(String s) {
        byte[] encodedBytes = Base64.encodeBase64(s.getBytes());
        return new String(encodedBytes);
    }
}
