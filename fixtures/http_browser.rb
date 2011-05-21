require "rubygems"
gem 'httparty'
require "httparty"

module Fixtures
  
  class HttpBrowser
    attr_accessor :method, :url
    attr_reader :message
    
    def initialize(host, port)
      @host = host
      @port = port
    end
        
    def success
      response = HTTParty.send(@method, "http://#{@host}:#{@port}#{@url}")
      @message = "none"
      return response.code == 200
    rescue Errno::ECONNREFUSED => e
      @message = "#{e.message}.  Are you sure your server is running on http://#{@host}:#{@port}?"
      return false
    end
    
  end
end
