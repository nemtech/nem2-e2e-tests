pipeline {
  agent {
    node {
      label 'server_ubuntu_20.04'
    }

  }
  stages {
    stage('Initialize') {
      parallel {
        stage('Initialize') {
          steps {
            echo 'First run'
          }
        }

        stage('Version') {
          steps {
            sh 'java  -version'
          }
        }

        stage('Install Gradle') {
          steps {
            sh '''sudo apt -y install vim apt-transport-https dirmngr wget software-properties-common
sudo add-apt-repository ppa:cwchien/gradle
sudo apt update
sudo apt -y install gradle'''
          }
        }

      }
    }

    stage('Git checkout') {
      steps {
        git(url: 'https://github.com/nemtech/nem2-e2e-tests.git', branch: 'main', changelog: true, credentialsId: 'a7aaa97a-dc50-4d20-bf6c-b7343bc44b1b')
      }
    }

    stage('Build test') {
      steps {
        sh '''cd symbol-e2e-tests/
gradle build -x test '''
      }
    }

  }
}