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

      }
    }

  }
}