@Library('shared_lib') _

pipeline {
    agent any

    triggers { githubPush() }

    options {
        buildDiscarder logRotator(numToKeepStr: '5')
        timeout(time: 10, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    tools {
        maven 'Maven_3.9.10'
    }

    environment {
        TOMCAT_SERVER_IP = "56.228.35.184"
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source'
                checkout scm
            }
        }

        stage('Maven Build and Sonar') {
            steps {
                withCredentials([string(credentialsId: 'sonar_secret', variable: 'sonar_secret')]) {
                    mavenBuild(sonar_secret)
                }
            }
        }

        stage('Upload to Nexus') {
            steps {
                sh "mvn clean deploy"
            }
        }

        stage('Deploy to Tomcat') {
            when {
                expression { return env.BRANCH_NAME == 'PERMAN' }
            }
            steps {
                deployToTomcat(env.TOMCAT_SERVER_IP, "target/student-reg-webapp.war", 'tomcat_1')
            }
        }
    }

    post {
        success {
            sendNotification("SUCCESS", "shirsathamit2025@gmail.com")
        }
        failure {
            sendNotification("FAILED", "shirsathamit2025@gmail.com")
        }
        always {
            cleanWs()
        }
    }
}
