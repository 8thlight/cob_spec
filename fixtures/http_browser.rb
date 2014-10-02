require "httparty"
require "nokogiri"
require "base64"

class HttpBrowser
  attr_accessor :host, :port, :status, :data, :etag
  attr_reader :response

  def get(url)
    @response = HTTParty.get("http://#{host}:#{port}#{url}")
    response_present?
  end

  def get_with_partial_header(url)
    @response = HTTParty.get("http://#{host}:#{port}#{url}", :headers => {"Range" => "bytes=0-4"})
    response_present?
  end

  def get_with_credentials(url)
    encoded_auth = Base64.encode64("admin:hunter2")
    @response = HTTParty.get("http://#{host}:#{port}#{url}", :headers => {"Authorization" => "Basic #{encoded_auth}"});
    response_present?
  end

  def head(url)
    @response = HTTParty.head("http://#{host}:#{port}#{url}")
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

  def options(url)
    @response = HTTParty.options("http://#{host}:#{port}#{url}")
    response_present?
  end

  def delete(url)
    @response = HTTParty.delete("http://#{host}:#{port}#{url}")
    response_present?
  end

  def patch(url)
    @response = HTTParty.patch("http://#{host}:#{port}#{url}", :headers => {"Content-Length" => "7", "If-Match" => etag}, :body => data)
    response_present?
  end

  def read_file(file)
    File.open(file, 'rb') { |f| f.read }
  end

  def response_present?
    !response.code.nil? && !response.code.zero?
  end

  def response_header_allow_contains(methods)
    expected_methods = methods.split(',')
    response_allow_methods = response.headers["allow"].split(',')
    expected_methods.all?{|method| response_allow_methods.include? method}
  end

  def response_code_equals(code)
    response.code == code.to_i
  end

  def body_has_content(content)
    response.body.include? content
  end

  def body_does_not_have_content(content)
    not response.body.include? content
  end

  def body_has_partial_file_contents(file)
    contents = read_file(file)
    response.body == contents[0..4]
  end

  def body_has_directory_contents(directory)
    entries = Dir.entries(directory).
      reject { |entry| entry.start_with?('.') }.
      all? { |entry| response.body.include? entry }
  end

  def body_has_link(path)
    links = Nokogiri::HTML(response.body).css('a').map { |value| value.to_s }
    links.include? "<a href=\"#{path}\">#{path}</a>" or links.include? "<a href=\"/#{path}\">#{path}</a>"
  end

  def read_file(file)
    File.open(file, 'rb') { |f| f.read }
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
