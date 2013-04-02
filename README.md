Cob Spec
========
Cob Spec is a suite of tests used to validate a web server to ensure it adheres to [HTTP specifications](http://www.w3.org/Protocols/rfc2616/rfc2616.html). These acceptance tests were created using a testing suite called [Finesse](http://fitnesse.org). FitNesse is an application testing suite that allows you to test the business layer of your application.

To test your server against the Cob Spec suite of tests, follow the instructions below.

    git clone git@github.com:8thlight/cob_spec.git
    git clone git@github.com:marosluuce/rubyslim.git
    cd cob_spec

    bundle install

Starting Fitnesse Server
------------------------
Start the Fitnesse server on port 9090.

<!-- code -->
    java -jar fitnesse.jar -p 9090

Open your browser and go to http://localhost:9090. You should see the Cob Spec website.

Configuring Cob Spec
-------------------
To run the tests you have to change three variables.

- Navigate to the HttpTestSuite.
- Click on Edit.
- Update the paths for the User-Defined Variables.
  - `TEST_RUNNER` is the path to the rubyslim repository clone on your machine.
  - `HTTP_SERVER_JAR` is the path to server jar file.
  - `PUBLIC_DIR` is the path to cob spec public folder.
- Click Save.

Http Server Jar
--------------
Your server jar needs to take two command line arguments.
- `-p` which specifies the port to listen on. Default is `5000`.
- `-d` which specifies the directory to serve files from. Default is the `PUBLIC_DIR` variable.

Running Tests
-------------
To run all tests, click the Suite button.
To run the simple http request tests, first click the ResponseTestSuite link, then click the Suite button.
To run the tests that require threading, first click the SimultaniousTestSuite link, then click the Suite button.
To run a test individually, first click on the test link, then click the Test button.
