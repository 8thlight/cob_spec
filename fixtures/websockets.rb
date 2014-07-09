require "websocket-eventmachine-client"

class Websockets
  attr_accessor :host, :port, :path

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

  def echoed_message_equals(message)
    @echoed_message == message
  end

  def pong_payload_equals(payload)
    @pong_payload == payload
  end
end
