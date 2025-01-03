pipeline {
  agent any
  stages {
    stage('Give Permissions') {
      steps {
        sh 'chmod +x -R ${WORKSPACE}'
      }
    }

    stage('Maven Package') {
      steps {
        sh './mvnw clean package -Pprod'
      }
    }

  }
  environment {
    DOCKER_IMAGE = 'project2-backend:latest'
  }
}