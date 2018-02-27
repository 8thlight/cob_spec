import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.http.client.config.AuthSchemes;
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
import java.net.URISyntaxException;
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

    public void bogusRequest(String url) throws IOException, URISyntaxException {
        Http browser = new Http(host, port);
        storeResponseInfoFrom(browser.bogusRequest(url));
    }

    public void getRangeStartRangeEnd(String url, String start, String end) throws IOException {
        Http browser = new Http(host, port);
        storeResponseInfoFrom(browser.getWithPartialHeader(url, "bytes=" + start + "-" + end));
    }

    public void getWithCredentials(String url) throws IOException {
        Http browser = new Http(host, port);
        storeResponseInfoFrom(browser.getWithCredentials(url, "admin", "hunter2"));
    }

    public void getWithCookie(String url) throws IOException {
        Http browser = new Http(host, port);
        Header setCookie = response.getFirstHeader("Set-Cookie");
        String cookie = setCookie.getValue();
        storeResponseInfoFrom(browser.getWithCookie(url, cookie));
    }

    public boolean responseCodeEquals(int code) {
        return latestResponseCode == code;
    }

    public boolean bodyHasContent(String content) throws IOException {
        return latestResponseContentAsString().contains(content);
    }

    public boolean bodyHasNoContent() throws IOException {
        return latestResponseContentAsString().isEmpty();
    }

    public boolean bodyHasFileContents(String filePath) throws IOException {
        byte[] fileContent = readBytesFromFile(filePath);
        return includes(fileContent, latestResponseContent);
    }

    private byte[] readBytesFromFile(String filePath) throws IOException {
        return Files.readAllBytes(Paths.get(filePath));
    }

    private boolean includes(byte[] first, byte[] second) {
        for(int i = 0; i < first.length; i++) {
            if (first[i] != second[i])
                return false;
        }
        return true;
    }

    public boolean bodyHasPartialFileContentsFromTo(String filePath, int from, int to) throws IOException {
        byte[] fileContent = readBytesFromFile(filePath);
        return Arrays.equals(
                latestResponseContent,
                Arrays.copyOfRange(fileContent, from, to + 1));
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
        return getHeaderValueFor(HttpHeaders.LOCATION);
    }

    public String contentRange() {
        return getHeaderValueFor(HttpHeaders.CONTENT_RANGE);
    }

    private String getHeaderValueFor(String name) {
        Header header = response.getFirstHeader(name);

        return (header != null) ? header.getValue() : "";
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

    public boolean hasBasicAuth() {
        Header authTypeHeader = response.getFirstHeader(HttpHeaders.WWW_AUTHENTICATE);
        return authTypeHeader != null && authTypeHeader.getValue().contains(AuthSchemes.BASIC);
    }

    public boolean contentTypeIs(String type) {
        Header contentTypeHeader = response.getFirstHeader(HttpHeaders.CONTENT_TYPE);
        if (contentTypeHeader == null)
            return false;

        String contentType = contentTypeHeader.getValue();

        return contentType.contains(type);
    }

    private void storeResponseInfoFrom(HttpResponse response) throws IOException {
        this.response = response;
        latestResponseCode = response.getStatusLine().getStatusCode();

        HttpEntity entity = response.getEntity();
        if (entity != null) {
            latestResponseContent = IOUtils.toByteArray(entity.getContent());
        } else {
            latestResponseContent = new byte[0];
        }
    }

    private String latestResponseContentAsString() {
        return new String(latestResponseContent);
    }
}
