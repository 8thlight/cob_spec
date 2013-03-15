require "httparty"

class HttpBrowser
  attr_accessor :host, :port, :status, :data
  attr_reader :response

  def get(url)
    @response = HTTParty.get("http://#{@host}:#{@port}#{url}")
  end

  def post(url)
    @response = HTTParty.post("http://#{host}:#{port}#{url}", :body => data)
  end

  def put(url)
    @response = HTTParty.put("http://#{host}:#{port}#{url}", :body => data)
  end

  def response_code_equals(code)
    response.code == code.to_i
  end

  def body_has_content(content)
    data.include? content
  end

  def body_has_directory_contents(directory)
    entries = Dir.entries(directory)
    entries.delete(".")
    entries.delete("..")
    entries.all? { |entry|
      @data.include? entry
    }
  end

  def body_has_link(path)
    not @data.match(/href=("|')[^'"]*\/#{path}("|')/).nil?
  end

  def body_has_file_contents(file)
    contents = File.open(file, 'rb') { |f| f.read }
    data.include? contents
  end

  def header_field_value(field)
    response.headers[field]
  end

  def last_request_path
    response.request.path.to_s
  end
end
