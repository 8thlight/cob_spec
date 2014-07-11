require "websocket-eventmachine-client"

class Websockets
  attr_accessor :host, :port, :path
  attr_reader :echoed_file, :echoed_message, :pong_payload

  def ping(payload)
    EM.run do
      ws = WebSocket::EventMachine::Client.connect(:uri => "ws://#{host}:#{port}#{path}")

      ws.onopen do
        ws.ping(payload)
      end

      ws.onpong do |message|
        @pong_payload = message

        EM.stop
      end
    end
  end

  def send_message(message)
    EM.run do
      ws = WebSocket::EventMachine::Client.connect(:uri => "ws://#{host}:#{port}#{path}")

      ws.onopen do
        ws.send(message)
      end

      ws.onmessage do |message|
        @echoed_message = message

        EM.stop
      end
    end
  end

  def send_file(file_name)
    file_contents = File.open(file_name, 'rb') { |f| f.read }

    EM.run do
      ws = WebSocket::EventMachine::Client.connect(:uri => "ws://#{host}:#{port}#{path}")

      ws.onopen do
        ws.send(file_contents, :type => :binary)
      end

      ws.onmessage do |message|
        @echoed_file = message

        EM.stop
      end
    end
  end

  def echoed_message_was(message)
    @echoed_message == message
  end

  def pong_payload_was(payload)
    @pong_payload == payload
  end

  def echoed_file_was(file_path)
    file_contents = File.open(file_path, 'rb') { |f| f.read }

    @echoed_file == file_contents
  end
end
