pipeline {
  agent any
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
            sh 'java -version'
          }
        }

      }
    }

  }
}
