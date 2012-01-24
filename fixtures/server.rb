module Fixtures

  class Server
    attr_accessor :path, :port, :directory
    
    def start_server
      @@pid = IO.popen("/usr/bin/java -jar #{@path} -p #{@port} -d #{@directory}").pid
      sleep(1)
    end
    
    def stop_server
      Process.kill("INT", @@pid)
    end
  end
end