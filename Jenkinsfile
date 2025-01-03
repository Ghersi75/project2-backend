pipeline {
  agent any
  stages {
    stage('Build Project') {
      steps {
        sh './mvnw clean package -Pprod'
      }
    }

    stage('Build Docker Image') {
      steps {
        sh '''docker build -t ${DOCKER_IMAGE} .
'''
      }
    }

  }
  environment {
    DOCKER_IMAGE = 'project2-backend:latest'
  }
}