package util;

import org.apache.http.client.methods.*;
import org.apache.http.ProtocolVersion;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;

public class BogusRequest extends HttpRequestBase {
    private final String PROTOCOL = "HTTP";

    private final URI uri;
    private final ProtocolVersion protocolVersion;

    public BogusRequest(String uri) throws URISyntaxException {
        this.uri = new URI(uri);
        this.protocolVersion = new ProtocolVersion(PROTOCOL, 1, 1);
    }

    public String getMethod() {
        String randomMethod = "";
        Random r = new Random();
        for(int i=0; i<8; i++)
            randomMethod += (char)(r.nextInt(26) + 'A');
        return randomMethod;
    }
  
    public URI getURI() {
        return uri;
    }

    public ProtocolVersion getProtocolVersion() {
        return protocolVersion;
    }
}
