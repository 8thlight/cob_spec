public class HttpBrowser {
  private String host;
  private String port;

  public void setHost(String host) {
    this.host = host;
  }

  public void setPort(String port) {
    this.port = port;
  }

  public void get(String url) {
  }

  public void getWithCredentials(String url) {
  }

  public void put(String url) {
  }

  public void head(String url) {
  }

  public boolean responseCodeEquals(int code) {
    return false;
  }

  public boolean bodyHasContent(String content) {
    return false;
  }


}
