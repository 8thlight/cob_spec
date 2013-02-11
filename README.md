Cob Spec
========

Cob Spec is a suite of tests used to validate a web server to ensure it adheres to [HTTP specifications](http://www.w3.org/Protocols/rfc2616/rfc2616.html). These acceptance tests were created using a testing suite called [Finesse](http://fitnesse.org). FitNesse is an application testing suite that allows you to test the business layer of your application.  

To test your server against the Cob Spec suite of tests, follow the instructions below.  

    git clone <repo>
    cd <path_to_project>/cob_spec

*Notes:*
- The .rvmrc file specifies `ruby-1.8.7-p302` and creates gemset `cob_spec`.
- If you already have a different patch of 1.8.7 installed and you encounter an error when you try to install this patch, try reinstalling 1.8.7 without the the tcl and tk libraries.
- Those libraries are not properly detected by 1.8.7, so you might have to reinstall 1.8.7 using the command below.

<!-- code -->
    rvm reinstall 1.8.7 --without-tcl --without-tk

At this point, `ruby-1.8.7-p302` should be installed.

    git submodule init
    git submodule update

    gem install bundler
    bundle install

Updating Suite Set Up Code
--------------------------

Within the `cob_spec` directory, you need to edit a few things.

    cd FitNesseRoot/HttpSuite/SuiteSetUp/

- Open the `content.txt` file located in this directory.
- Update it with information specific to the server you want to test.
- Change the path to be set to the path to your JAR file.
- Change the directory to be set to `public`.
- `public` is a directory within the `cob_spec` directory that contains files that your server should be able to serve.
- Make sure your server starts on port 5000.

Starting Fitnesse Server
------------------------

Start the Fitnesse server on port 9090.

<!-- code -->
    java -jar fitnesse.jar -p 9090

Open your browser and go to http://localhost:9090. You should see the Cob Spec website.  

Running Tests
-------------

To test against the simple http request tests, first click the .HttpSuite link, then click the Suite button.  
To test your server's ability to handle simultaneous requests, first click the SimultaneousRequests link, then click the Test button.  
To test the time to complete requests, first click the TimeToComplete link, then click the Test button.
