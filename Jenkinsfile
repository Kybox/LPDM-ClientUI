pipeline {
    agent any
    tools {
        maven 'Apache Maven 3.5.2'
    }
    stages{
        stage('Checkout') {
            steps {
                git 'https://github.com/Kybox/LPDM-ClientUI'
            }
        }
        stage('Tests') {
            steps {
                sh 'mvn clean test'
            }
            post {
                always {
                    junit 'target/surefire-reports/**/*.xml'
                }
                failure {
                    error 'The tests failed'
                }
            }
        }
        stage('Push to DockerHub') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage('Deploy') {
            steps {
                sh "docker stop LPDM-ClientUI || true && docker rm LPDM-ClientUI || true"
                sh "docker pull vyjorg/lpdm-user:latest"
                sh "docker run -d --name LPDM-ClientUI -p 30000:28082 --restart always --memory-swappiness=0 kybox/lpdm-clientui:latest"
            }
        }
    }
}