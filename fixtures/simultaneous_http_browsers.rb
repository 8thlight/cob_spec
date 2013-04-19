require "typhoeus"

class SimultaneousHttpBrowsers
  attr_writer :host, :port
  attr_reader :time, :requests, :response_codes, :amount

  def initialize
    @response_codes = {}
    @response_codes.default = 0
    @hydra = Typhoeus::Hydra.new
  end

  def add_requests(url, amount=1)
    (0...amount.to_i).each do
      request = Typhoeus::Request.new("http://#{@host}:#{@port}#{url}")
      request.on_complete do |response|
        response_codes[response.code] += 1
      end

      @hydra.queue request
    end
    @amount = @hydra.queued_requests.count
  end

  def execute
    start_time = Time.now
    @hydra.run
    @time = Time.now - start_time
  end

  def all_response_codes_equal(code)
    response_codes.each do |status_code, count|
      puts "Status Code:#{status_code} - Count:#{count}"
    end

    response_codes[code.to_i] == amount
  end
end
