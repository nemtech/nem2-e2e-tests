# Catapult server test

## Welcome

Catapult server tests are integration and e2e tests for the Catapult server.

## Contributing

Before contributing please [read this](CONTRIBUTING.md).

## Installation Instructions

1. Clone this git repro - git clone --recursive https://github.com/nemtech/nem2-e2e-tests.git
2. Install Java 8 - https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html
3. Install Maven - https://maven.apache.org/install.html
4. Install Cucumber-JVM in not using Maven/Intellij to build - https://docs.cucumber.io/installation/java/
5. Install Intellij IDE and Cucumber plugin if you want to run the test from IDE.
   Intellij - https://www.jetbrains.com/idea/download/#section=mac
   Cucumber plugin - https://plugins.jetbrains.com/plugin/7212-cucumber-for-java  

## Setup environment to run tests
1) Get the latest catapult-service-bootstrap - https://github.com/tech-bureau/catapult-service-bootstrap
2) Start the service with the dev docker compose file - docker-compose -f docker-compose-with-explorer-dev.yml up
3) Update the some of the properties in the integrationtests/src/test/resources/configs/config-default.properties file to match your bootstrap environment
    - The apiServerKey property - This is the public key of the API server and can be found in build/catapult-config/api-node-0/userconfig/resources/peers-api.json
    - The userKey property - This is the private key of the user which will be use to sign each transaction.
        A list of users can be found in in the build/generated-addresses/addresses.yaml file under the nemesis_addresses section.
    - Update the mosaicId to match that of the userKey above.  You can find the mosaic id of the user by going to http://localhost:8000/#/account/<public key of user>


## Running tests

1) Go to the folder where you stored the repository
2) cd integrationtests
3) run 'mvn test' to build and run the tests
4) If you install the IDE, the tests can also be run and debug from there.

## Adding new tests

The file structure of the automation tests are as follows

Feature files -  integrationtests/src/test/resources/io/nem
Cucumber steps files - integrationtests/src/test/java/io/nem/automation.

In each of these folders there is an example folder which has a feature and cucumber steps file respectively.
  
Before adding tests you should check the Nem2 Scenarios repro(https://github.com/nemtech/nem2-scenarios) for a list of Cucumber feature files. These feature files needs to be automation and should be added first.

To check if a feature file from the nem2 scenarios is already automation, check if the feature file is present in this repro. 
