# Symbol tests

Various tests for the symbol project. This repository aims to test the Symbol server. For portability and ease of execution reasons, the symbol bootstrap is used which runs Symbol in a set of docker-compose services. Testing  categories include:
1. End-to-end tests written in the form of user behaviours described in Gherkin and automated using Cucumber JVM
2. Resilience tests written in bash using a popular tool pumba

More test categories and tests in each category will be added.

## Prerequisites

* [Docker](https://docs.docker.com/install/linux/docker-ce/centos/) - 
Docker is a pre-requisite for running Symbol bootstrap anyway.

* [Docker Compose](https://docs.docker.com/compose/) - 
Docker Compose is also a pre-requisite for running Symbol bootstrap anyway.

* [Python 3](https://www.python.org/about/) & [Pip](https://pip.pypa.io/en/stable/installing/) - 
Resilience tests use a combination of bash and python scripts

* [Java 8](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html) - Required for building and executing the Cucumber features and scenarios

* [Gradle](https://gradle.org/install/) - Build and automation tool
  
You could use the `install-gradle.sh` script provided at the root of the repository to install gradle from the commandline

## Installation and execution

Please refer to the corresponding read me for specific instructions on how to build and execute.

## Contributing

Before contributing please [read this](CONTRIBUTING.md).
