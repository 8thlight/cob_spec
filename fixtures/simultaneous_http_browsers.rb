require "rubygems"
gem 'typhoeus'
require "typhoeus"

module Fixtures

  class SimultaneousHttpBrowsers
    attr_writer :host, :port

    def initialize
       @urls = Array.new
       @responses = Array.new
    end

    def add_url url
      (0..100).each do
      puts url.inspect
          request = Typhoeus::Request.new("http://#{@host}:#{@port}#{url}",
                                          :method => :get)
          request.on_complete do |response|
             @responses << response.code
          end
          @urls << request 
      end
    end

    def execute
       hydra = Typhoeus::Hydra.new
       @urls.each do |request|
          hydra.queue request
       end
       hydra.run
    end

    def all_ok_response
       @responses.each do |code|
          return false if code != 200
       end
       return true
    end

  end
end  
