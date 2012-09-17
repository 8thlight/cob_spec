module Fixtures

  class Server
    attr_accessor :path, :port, :directory
    
    def start_server
      @@pid = IO.popen("#{start_command} -p #{@port} -d #{@directory}").pid
      sleep(5)
    end
    
    def stop_server
      Process.kill("INT", @@pid)
    end


    private 
    
    def start_command
      if java? then return "/usr/bin/java -jar #{@path}" end
      @path
    end

    def java?
      @path.end_with? ".jar"
    end

  end
end
