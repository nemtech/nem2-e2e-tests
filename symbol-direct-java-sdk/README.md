# Symbol Direct Java SDK

An SDK which gives you direct access to the symbol server - mainly for testing purposes. This java library is a dependency for the Symbol integration and e2e tests.

## Prerequisites

* [Java 8](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html)
* [Gradle](https://gradle.org/install/)

## Installation

1. Clone this repository:

```bash
git clone --recursive https://github.com/nemtech/nem2-e2e-tests.git
```

## Building the SDK

1. Open the folder where you have cloned this repository.

2. Build

```bash
gradle --project-dir symbol-direct-java-sdk/ clean build
```

**Note**: If you have installed an IDE, you cnan run the tests and debug them from there.

## Contributing

Before contributing please [read this](CONTRIBUTING.md).
