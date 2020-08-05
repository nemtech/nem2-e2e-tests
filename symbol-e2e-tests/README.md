# Symbol end-to-end behaviour tests

Integration and e2e tests for the symbol project.

## Prerequisites

* [Java 8](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html)
* [Gradle](https://gradle.org/install/) 
  
You could use the `install-gradle.sh` script provided at the root of the repository to install gradle from the commandline

## Installation

1. Clone this repository:

```bash
git clone --recursive https://github.com/nemtech/symbol-e2e-tests.git
```

2. Install the [Cucumber-JVM](https://docs.cucumber.io/installation/java/). You can install the [Cucumber plugin](https://plugins.jetbrains.com/plugin/7212-cucumber-for-java) for IntelliJ if you want to run the test from the IDE.

3. Get the latest [catapult-service-bootstrap](https://github.com/tech-bureau/catapult-service-bootstrap) and start the service.

4. Update the following properties under the ``integrationtests/src/test/resources/configs/config-default.properties`` file to match your bootstrap environment.
    - ``apiServerPublicKey``: Public key of the API server. You can find it in ``build/catapult-config/api-node-0/userconfig/resources/peers-api.json``.
    - ``userPrivateKey``: Private key of the user which will be use to sign each transaction. You can find a list of users in ``build/generated-addresses/addresses.yaml`` file under the ``nemesis_addresses`` section.
    - ``automationPrivateKey``: If automation is running on the same host as the api server(i.e. same IP) then the set to api server boot private key.  Otherwise it can be any private key.
	- ``harvesterPublicKey``: Hasvester's public key of the specified Hasvester in the config-harvesting.properties file.
	- ``restGatewayUrl`` : Restway url. The default is http://localhost:3000
	- ``RepositoryFactoryType`` : This specify how automation communicates with the symbol server.  ``Direct`` tranactions are sent directly to the server.  ``Vertx`` uses the restway to submit transactions to the symbol server. 

## Running the tests

1. Open the folder where you have cloned this repository.

2. Build and run the tests.

```bash
gradle --project-dir symbol-e2e-tests/ clean test

```

**Note**: If you have installed an IDE, you can run the tests and debug them from there.

## Contributing

Before contributing please [read this](CONTRIBUTING.md).

### Adding new tests

The file structure of the automation tests is as follows:

* Feature files: ``symbol-e2e-tests/src/test/resources/io/nem/symbol``.
* Cucumber steps files: ``symbol-e2e-tests/src/test/java/io/nem/symbol/automation``.

In each of these folders, there is an example folder which has a feature and cucumber steps file respectively.
  
Before adding tests, check the [symbol-scenarios repository](https://github.com/nemtech/synbol-scenarios) for a list of Cucumber feature files already defined. This repository gathers the set of scenarios that should be automated.

To check if a feature file from the symbol scenarios is already automated, check if the feature file is present in this repository.