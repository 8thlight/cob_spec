require "typhoeus"

class SimultaneousHttpBrowsers
  attr_writer :host, :port, :amount
  attr_reader :time

  def initialize
    @requests = []
    @response_codes = {}
    @response_codes.default = 0
  end

  def add_url url
    (0...@amount.to_i).each do
      request = ::Typhoeus::Request.new("http://#{@host}:#{@port}#{url}", :method => :get)
      request.on_complete do |response|
        @response_codes[response.code] += 1
      end
      @requests << request
    end
  end

  def execute
    hydra = Typhoeus::Hydra.new
    @requests.each do |request|
      hydra.queue request
    end
    @time = Time.now
    hydra.run
    @time = Time.now - @time
  end

  def all_ok_response
    @response_codes.each do |code, count|
      puts "Status Code:#{code} - Count:#{count}"
    end

    @response_codes[200] == @requests.count ? true : false
  end

end
