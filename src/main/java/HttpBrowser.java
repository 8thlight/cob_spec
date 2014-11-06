import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class HttpBrowser {
    private String host;
    private String data;
    private String eTag;
    private String location;
    private int port;
    private int latestResponseCode;
    private byte[] latestResponseContent;

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(String port) {
        this.port = Integer.parseInt(port);
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setEtag(String eTag) {
        this.eTag = eTag;
    }

    public void get(String url) throws IOException {
        makeStandardRequest(new HttpGet(fullUrlFrom(url)));
    }

    public void put(String url) throws IOException {
        HttpPut put = new HttpPut(fullUrlFrom(url));
        put.setEntity(new ByteArrayEntity(dataAsByteArray()));
        makeStandardRequest(put);
    }

    public void head(String url) throws IOException {
        makeStandardRequest(new HttpHead(fullUrlFrom(url)));
    }

    public void post(String url) throws IOException {
        HttpPost post = new HttpPost(fullUrlFrom(url));
        post.setEntity(new ByteArrayEntity(dataAsByteArray()));
        makeStandardRequest(post);
    }

    public void patch(String url) throws IOException {
        HttpPatch patch = new HttpPatch(fullUrlFrom(url));
        patch.setHeader(HttpHeaders.IF_MATCH, eTag);
        patch.setEntity(new ByteArrayEntity(dataAsByteArray()));
        makeStandardRequest(patch);
    }

    public void delete(String url) throws IOException {
        makeStandardRequest(new HttpDelete(fullUrlFrom(url)));
    }

    public void getWithPartialHeader(String url) throws IOException {
        HttpClient client = HttpClients.custom().build();
        HttpUriRequest request = RequestBuilder
                .get()
                .setUri(fullUrlFrom(url))
                .setHeader(HttpHeaders.RANGE, "bytes=0-4")
                .build();
        storeResponseInfoFrom(client.execute(request));
    }

    public void getWithCredentials(String url) throws IOException {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope("localhost", port),
                new UsernamePasswordCredentials("admin", "hunter2"));
        HttpClient client = HttpClientBuilder.create()
                .setDefaultCredentialsProvider(credsProvider)
                .build();

        executeRequest(client, new HttpGet(fullUrlFrom(url)));
    }

    public String location() {
        return location;
    }

    public boolean responseCodeEquals(int code) {
        return latestResponseCode == code;
    }

    public boolean bodyHasContent(String content) throws IOException {
        return latestResponseContentAsString().contains(content);
    }

    public boolean bodyHasFileContents(String filePath) throws IOException {
        byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
        return Arrays.equals(latestResponseContent, fileContent);
    }

    public boolean bodyHasPartialFileContents(String filePath) throws IOException {
        byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
        return Arrays.equals(
                latestResponseContent,
                Arrays.copyOfRange(fileContent, 0, 4));
    }

    public boolean bodyHasLink(String link) {
        String linkSelector = String.format("a[href=/%s]:contains(%s)", link, link);
        Document doc = Jsoup.parse(latestResponseContentAsString());
        Elements links = doc.select(linkSelector);
        return links.size() > 0;
    }

    public boolean bodyHasDirectoryContents(String directoryPath) throws IOException {
        File directory = new File(directoryPath);
        String[] files = directory.list(HiddenFileFilter.VISIBLE);

        for (String file : files) {
            if (!latestResponseContentAsString().contains(file))
                return false;
        }
        return true;
    }

    private String fullUrlFrom(String url) {
        return "http://" + host + ":" + port + url;
    }

    private void makeStandardRequest(HttpRequestBase request) throws IOException {
        HttpClient client = HttpClientBuilder
                .create()
                .disableRedirectHandling()
                .build();
        executeRequest(client, request);
    }

    private void executeRequest(HttpClient client, HttpRequestBase request) throws IOException {
        HttpResponse response = client.execute(request);
        storeResponseInfoFrom(response);
    }

    private void storeResponseInfoFrom(HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        Header locationHeader = response.getFirstHeader("location");

        if (locationHeader != null)
            location = locationHeader.getValue();

        if (entity != null) {
            latestResponseContent = IOUtils.toByteArray(entity.getContent());
            latestResponseCode = response.getStatusLine().getStatusCode();
        }
    }

    private String latestResponseContentAsString() {
        return new String(latestResponseContent);
    }

    private byte[] dataAsByteArray() {
        return (data != null) ? data.getBytes() : "".getBytes();
    }
}
