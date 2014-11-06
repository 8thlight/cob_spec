import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import util.Http;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class HttpBrowser {
    private String host;
    private String data;
    private String eTag;
    private int port;
    private HttpResponse response;
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
        Http browser = new Http(host, port);
        storeResponseInfoFrom(browser.get(url));
    }

    public void put(String url) throws IOException {
        Http browser = new Http(host, port);
        storeResponseInfoFrom(browser.put(url, data));
    }

    public void head(String url) throws IOException {
        Http browser = new Http(host, port);
        storeResponseInfoFrom(browser.head(url));
    }

    public void post(String url) throws IOException {
        Http browser = new Http(host, port);
        storeResponseInfoFrom(browser.post(url, data));
    }

    public void patch(String url) throws IOException {
        Http browser = new Http(host, port);
        storeResponseInfoFrom(browser.patch(url, data, eTag));
    }

    public void delete(String url) throws IOException {
        Http browser = new Http(host, port);
        storeResponseInfoFrom(browser.delete(url));
    }

    public void options(String url) throws IOException {
        Http browser = new Http(host, port);
        storeResponseInfoFrom(browser.options(url));
    }

    public void getWithPartialHeader(String url) throws IOException {
        Http browser = new Http(host, port);
        storeResponseInfoFrom(browser.getWithPartialHeader(url, "bytes=0-4"));
    }

    public void getWithCredentials(String url) throws IOException {
        Http browser = new Http(host, port);
        storeResponseInfoFrom(browser.getWithCredentials(url, "admin", "hunter2"));
    }

    public boolean responseCodeEquals(int code) {
        return latestResponseCode == code;
    }

    public boolean bodyHasContent(String content) throws IOException {
        return latestResponseContentAsString().contains(content);
    }

    public boolean bodyHasFileContents(String filePath) throws IOException {
        byte[] fileContent = readBytesFromFile(filePath);
        return Arrays.equals(latestResponseContent, fileContent);
    }

    private byte[] readBytesFromFile(String filePath) throws IOException {
        return Files.readAllBytes(Paths.get(filePath));
    }

    public boolean bodyHasPartialFileContents(String filePath) throws IOException {
        byte[] fileContent = readBytesFromFile(filePath);
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

    public String location() {
        Header locationHeader = response.getFirstHeader("location");

        return (locationHeader != null) ? locationHeader.getValue() : "";
    }

    public boolean responseHeaderAllowContains(String csvAllows) {
        String[] allows = csvAllows.split(",");
        Header responseAllows = response.getFirstHeader(HttpHeaders.ALLOW);
        String[] serverAllows = responseAllows.getValue().split(",");

        for (String  allow : allows) {
            boolean containsString = false;
            for (String serverAllow : serverAllows ) {
                if (serverAllow.equals(allow)) {
                    containsString = true;
                    break;
                }
            }
            if (!containsString)
                return false;
        }

        return true;
    }

    private void storeResponseInfoFrom(HttpResponse response) throws IOException {
        this.response = response;
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            latestResponseContent = IOUtils.toByteArray(entity.getContent());
            latestResponseCode = response.getStatusLine().getStatusCode();
        }
    }

    private String latestResponseContentAsString() {
        return new String(latestResponseContent);
    }
}
