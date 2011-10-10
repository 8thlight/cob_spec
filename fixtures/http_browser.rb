require "rubygems"
gem 'httparty'
require "httparty"

module Fixtures

  class HttpBrowser
    attr_accessor :host, :port, :status, :data
    attr_reader :response

    def get(url)
      @response = HTTParty.get("http://#{@host}:#{@port}#{url}")
      @message = response.message
      @status =  response.code
    rescue Errno::ECONNREFUSED => e
      @message = "#{e.message}.  Are you sure your server is running on http://#{@host}:#{@port}?"
    end

    def post(url)
      @response = HTTParty.post("http://#{@host}:#{@port}#{url}", :body => @data)
      @message = response.message
      @status = response.code
    rescue Errno::ECONNREFUSED => e
      @message = "#{e.message}.  Are you sure your server is running on http://#{@host}:#{@port}?"
    end

    def ok_response
      return @status == 200
    end

    def not_found_response
      return @status == 404
    end
  end
end
