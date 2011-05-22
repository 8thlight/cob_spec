require "rubygems"
gem 'httparty'
require "httparty"

module Fixtures
  
  class HttpBrowser
    attr_accessor :method, :url, :host, :port
    attr_reader :message
    
    def get(url)    
      response = HTTParty.get("http://#{@host}:#{@port}#{url}")
      @message = "none"
      @success =  response.code == 200
    rescue Errno::ECONNREFUSED => e
      @message = "#{e.message}.  Are you sure your server is running on http://#{@host}:#{@port}?"
      @success = false
    end
    
    def success
      return @success
    end
    
    def set_host(host)
      @host = host
    end
    
    def set_port(port)
      @port = port
    end
    
  end
end
