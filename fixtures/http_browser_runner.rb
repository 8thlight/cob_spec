require_relative '../fixtures/http_browser'

http_browser = HttpBrowser.new
http_browser.host = "localhost"
http_browser.port = 5000

http_browser.get_with_partial_header("/partial_content.txt")

