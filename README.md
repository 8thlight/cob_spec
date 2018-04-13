Cob Spec
========
Cob Spec is a suite of tests used to validate a web server to ensure it adheres to [HTTP specifications](https://tools.ietf.org/html/rfc7230). These acceptance tests were created using a testing suite called [FitNesse](http://fitnesse.org). FitNesse is an application testing suite that allows you to test the business layer of your application.

Setup
------------

Cob Spec requires [Maven](https://maven.apache.org/install.html) and [JDK 8](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html) to be installed to run correctly.  

Getting Started
----------------

To test your server against the Cob Spec suite of tests, follow the instructions below.

    git clone git@github.com:8thlight/cob_spec.git
    cd cob_spec
    mvn package  
  
Starting Fitnesse Server
------------------------
Start the Fitnesse server on port 9090.

<!-- code -->
    java -jar fitnesse.jar -p 9090

Open your browser and go to http://localhost:9090. You should see the Cob Spec website.

Configuring Cob Spec
-------------------
To run the tests you have to change some variables.

- Navigate to the HttpTestSuite.
- Click on Edit.
- Update the paths for the User-Defined Variables.
  - `SERVER_START_COMMAND` is the command to start your server.
    - Example: `java -jar /User/somebody/project/my_jar.jar`
  - `PUBLIC_DIR` is the path to cob spec public folder.
    - Example: `/User/somebody/cob_spec/public/`
- Note that you have to remove the `-` at the beginning of the line in order for
  the User-Defined Variables to be recognized.
- Click Save.

Http Server
--------------
Your server jar needs to take two command line arguments.
- `-p` which specifies the port to listen on. Default is `5000`.
- `-d` which specifies the directory to serve files from. Default is the `PUBLIC_DIR` variable.

Running Tests
-------------
- To run all tests, click the Suite button.
- To run the simple http request tests, first click the ResponseTestSuite link, then click the Suite button.
- To run the tests that require threading, first click the SimultaniousTestSuite link, then click the Suite button.
- To run a test individually, first click on the test link, then click the Test button.

Adding CI
------------
- Fork cob spec
- On your local machine navigate to your server's directory
- Create a submodule using the forked copy of cob spec `git submodule add https://github.com/myusername/cob_spec`
- Within this new submodule navigate to `FitNesseRoot` and create a new directory `mkdir PassingTests`
- Copy contents of `HttpTestSuite` into your new directory `PassingTests` by issuing the following command: `cp -r HttpTestSuite/ PassingTests/`
- Navigate into `PassingTests/ResponseTestSuite` and add/remove cob spec tests as necessary
- To add a test to the `PassingTests`: `cp -r HttpTestSuite/ResponseTestSuite/SimpleGet PassingTests/ResponseTestSuite/SimpleGet/`
- To remove a test in `PassingTests`: `rm -rf PassingTests/ResponseTestSuite/SimpleGet/`
- Navigate to your server's directory. Create a new `.sh` file `touch run_cob_spec_tests.sh`
- Open the file and add the following:

  `#!/bin/bash`

  ` set -e`

  `cd cob_spec`

  `mvn package`

   `java -jar fitnesse.jar -c "PassingTests?suite&format=test"`

- The following CI instructions are for TravisCI but follow a similar pattern for other CI platforms. In .travis.yml:
    `before_install:`

    `   - chmod +x run_cob_spec_tests.sh`

    `script:`

    `   - "./run_cob_spec_tests.sh"`
- Push branch and run the build
