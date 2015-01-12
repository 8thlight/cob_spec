class Server
  attr_accessor :start_command, :port, :directory

  def start_server
    @@pid = IO.popen("#{start_command} -p #{port} -d #{directory}").pid
    sleep(2)
  end

  def stop_server
    Process.kill("INT", @@pid)
  end
end
