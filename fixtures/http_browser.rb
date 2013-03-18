require "httparty"

class HttpBrowser
  attr_accessor :host, :port, :status, :data
  attr_reader :response

  def get(url)
    @response = HTTParty.get("http://#{@host}:#{@port}#{url}")
    response_present?
  end

  def post(url)
    @response = HTTParty.post("http://#{host}:#{port}#{url}", :body => data)
    response_present?
  end

  def put(url)
    @response = HTTParty.put("http://#{host}:#{port}#{url}", :body => data)
    response_present?
  end

  def response_present?
    not (response.nil? || response.empty?)
  end

  def response_code_equals(code)
    response.code == code.to_i
  end

    def method_not_allowed_response
       return @status == 405
    end

    def partial_content_response
       return @status == 206
    end

    def body_has_content(content)
      @data.include? content
    end

  def body_has_directory_contents(directory)
    entries = Dir.entries(directory)
    entries.delete(".")
    entries.delete("..")
    entries.all? { |entry| response.body.include? entry }
  end

  def body_has_link(path)
    not response.body.match(/href=("|')[^'"]*\/#{path}("|')/).nil?
  end

    def read_file(file)
       File.open(file, 'rb') { |f| f.read }
    end

    def body_has_file_contents(file)
      contents = read_file(file)
      @data.include? contents
    end

    def get_with_partial_header(url)
      @response = HTTParty.get("http://#{@host}:#{@port}#{url}", :headers => {"Range: " => "bytes=0-4"})
      @message = response.message
      @status = response.code
      @data = response.body
      rescue Errno::ECONNREFUSED => e
       econnrefused e
    end

    def body_has_partial_file_contents(file)
      contents = read_file(file)
      @data == contents[0..3]
    end

    def header_field_value(field)
      return @response.headers[field]
    end

  def last_request_path
    response.request.path.to_s
  end
end
