require "httparty"
require "nokogiri"

class HttpBrowser
  attr_accessor :host, :port, :status, :data
  attr_reader :response

  def get(url)
    @response = HTTParty.get("http://#{host}:#{port}#{url}")
    response_present?
  end

  def get_with_partial_header(url)
    @response = HTTParty.get("http://#{host}:#{port}#{url}", :headers => {"Range: " => "bytes=0-4"})
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

  def read_file(file)
    File.open(file, 'rb') { |f| f.read }
  end

  def response_present?
    !response.code.nil?
  end

  def response_code_equals(code)
    response.code == code.to_i
  end

  def body_has_content(content)
    response.body.include? content
  end

  def body_has_partial_file_contents(file)
    contents = read_file(file)
    response.body == contents[0..3]
  end

  def body_has_directory_contents(directory)
    entries = Dir.entries(directory)
    entries.delete(".")
    entries.delete("..")
    entries.all? { |entry| response.body.include? entry }
  end

  def body_has_link(path)
    links = Nokogiri::HTML(response.body).css('a').map { |value| value.to_s }
    links.include? "<a href=\"/#{path}\">#{path}</a>"
  end

  def body_has_file_contents(file)
    contents = read_file(file)
    response.body.include? contents
  end

  def header_field_value(field)
    response.headers[field]
  end

  def last_request_path
    response.request.path.to_s
  end
end
