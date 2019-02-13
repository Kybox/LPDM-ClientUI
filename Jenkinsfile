pipeline {
    agent any
    tools {
        maven 'Apache Maven 3.5.2'
    }
    environment {
        KEY = ''
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
        stage('Load Key') {
            steps {
                script {
                    configFileProvider([configFile(fileId: 'a7bba115-f683-48ba-bfb0-1f81d142d2cc', targetLocation: 'lpdm.key')]) {
                        lpdm_keys = readJSON file: 'lpdm.key'
                        KEY = lpdm_keys.lpdm
                    }
                }
            }
        }
        stage('Deploy') {
            steps {
                sh "docker stop LPDM-ClientUI || true && docker rm LPDM-ClientUI || true"
                sh "docker pull kybox/lpdm-clientui:latest"
                sh "docker run -d --name LPDM-ClientUI -p 30000:30000 --restart always --memory-swappiness=0 -e 'JAVA_TOOL_OPTIONS=-Djasypt.encryptor.password=$KEY' kybox/lpdm-clientui:latest"
            }
        }
    }
}