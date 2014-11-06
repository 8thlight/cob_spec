import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpBrowser {
    private String host;
    private int port;
    private String latestResponseBody;
    private int latestResponseCode;

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(String port) {
        this.port = Integer.parseInt(port);
    }

    public void get(String url) throws IOException {
        makeStandardRequest(new HttpGet(getRequestFrom(url)));
    }

    public void put(String url) throws IOException {
        makeStandardRequest(new HttpPut(getRequestFrom(url)));
    }

    public void head(String url) throws IOException {
        makeStandardRequest(new HttpHead(getRequestFrom(url)));
    }

    public void getWithCredentials(String url) throws IOException {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope("localhost", port),
                new UsernamePasswordCredentials("admin", "hunter2"));
        HttpClient client = HttpClientBuilder.create()
                .setDefaultCredentialsProvider(credsProvider)
                .build();

        HttpResponse response = client.execute(new HttpGet("http://" + host + ":" + port + url));
        storeResponseInfoFrom(response);
    }

    public boolean responseCodeEquals(int code) {
        return latestResponseCode == code;
    }

    public boolean bodyHasContent(String content) throws IOException {
        return latestResponseBody.contains(content);
    }

    public boolean bodyHasLink(String link) {
        String linkSelector = String.format("a[href=/%s]:contains(%s)", link, link);
        Document doc = Jsoup.parse(latestResponseBody);
        Elements links = doc.select(linkSelector);
        return links.size() > 0;
    }

    public boolean bodyHasDirectoryContents(String directoryPath) throws IOException {
        File directory = new File(directoryPath);
        String[] files = directory.list(HiddenFileFilter.VISIBLE);
        for (String file : files) {
            if (!latestResponseBody.contains(file))
                return false;
        }
        return true;
    }

    private String getRequestFrom(String url) {
        return "http://" + host + ":" + port + url;
    }

    private void makeStandardRequest(HttpRequestBase request) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(request);
        storeResponseInfoFrom(response);
    }

    private void storeResponseInfoFrom(HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            latestResponseBody = EntityUtils.toString(entity);
            latestResponseCode = response.getStatusLine().getStatusCode();
        }
    }
}
