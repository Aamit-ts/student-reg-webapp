pipeline {
    agent any

   triggers {
       githubPush()
     }

   options {
      buildDiscarder logRotator(numToKeepStr: '5')
      timeout(time: 10, unit: 'MINUTES')
      disableConcurrentBuilds()
    }

    tools {
        maven 'Maven_3.9.10'
    }

    environment {
        // SONARQUBE_URL = "http://13.57.206.25:9000" // Optional
        TOMCAT_SERVER_IP = "13.60.43.145"
    }

    stages {
        stage('stage_1 Cloning Source Code From GitRepo') {
            steps {
                echo 'Git Clone'
                git branch: 'New_Branch',
                    credentialsId: '893d49a3-02b3-4657-b8b9-865a98636a90',
                    url: 'https://github.com/Aamit-ts/student-reg-webapp.git'
            }
        }

        stage('stage_2 Maven verify') {
            steps {
                withCredentials([string(credentialsId: 'sonar_secret', variable: 'sonar_secret')]) {
                    sh "maven1 clean package sonar:sonar -Dsonar.login=${sonar_secret}"
                }
            }
        }

        stage('stage_3 Upload package to Nexus') {
            steps {
                sh "mvn clean deploy"
            }
        }

        stage('stage_4 Deploy package to Tomcat') {
            steps {
                sshagent(['tomcat_1']) {
                    sh "scp -o StrictHostKeyChecking=no target/student-reg-webapp.war ec2-user@${TOMCAT_SERVER_IP}:/opt/tomcat/webapps/student-reg-webapp.war"
                }
            }
        }
    }

post {
        always {
            cleanWs()
        }
        success {
       //slackSend (channel: 'lic-appteam', color: "good", message: "Build - SUCCESS : ${env.JOB_NAME} #${env.BUILD_NUMBER} - URL: ${env.BUILD_URL}")
          sendEmail(
           "${env.JOB_NAME} - ${env.BUILD_NUMBER} - Build SUCCESS",
           "Build SUCCESS. Please check the console output at ${env.BUILD_URL}",
           'shirsathamit2025@gmail.com' )
        }
        failure {
         //slackSend (channel: 'lic-appteam', color: "danger", message: "Build - FAILED : ${env.JOB_NAME} #${env.BUILD_NUMBER} - URL: ${env.BUILD_URL}")
         sendEmail(
           "${env.JOB_NAME} - ${env.BUILD_NUMBER} - Build FAILED",
           "Build FAILED. Please check the console output at ${env.BUILD_URL}",
           'shirsathamit2025@gmail.com' )
        }
    }
}

def sendEmail(String subject, String body, String recipient) {
    emailext(
        subject: subject,
        body: body,
        to: recipient,
        mimeType: 'text/html'
    )
}
