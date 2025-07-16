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
        TOMCAT_SERVER_IP = "56.228.35.184"
    }

    stages {

        stage('stage_1 Cloning Source Code From GitRepo') {
            steps {
                echo 'Cloning using checkout scm (multibranch pipeline)'
                checkout scm
            }
        }

        stage('stage_2 Maven verify') {
            steps {
                withCredentials([string(credentialsId: 'sonar_secret', variable: 'sonar_secret')]) {
                    sh "mvn clean package sonar:sonar -Dsonar.login=${sonar_secret}"
                }
            }
        }

        stage('stage_3 Upload package to Nexus') {
            steps {
                sh "mvn clean deploy"
            }
        }

        stage('stage_4 Deploy package to Tomcat') {
            when {
                expression {
                    return env.BRANCH_NAME == 'PERMAN'
                }
            }
            steps {
                echo "Branch is ${env.BRANCH_NAME}, deploying to Tomcat..."
                sshagent(['tomcat_1']) {
                    sh """
                        ssh -o StrictHostKeyChecking=no ec2-user@${TOMCAT_SERVER_IP} '
                            sudo rm -rf /opt/tomcat/webapps/student-reg-webapp &&
                            sudo rm -f /opt/tomcat/webapps/student-reg-webapp.war
                        '
                        scp -o StrictHostKeyChecking=no target/student-reg-webapp.war ec2-user@${TOMCAT_SERVER_IP}:/opt/tomcat/webapps/student-reg-webapp.war
                    """
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }

        success {
            sendEmail(
                "${env.JOB_NAME} - ${env.BUILD_NUMBER} - Build SUCCESS",
                "Build SUCCESS. Please check the console output at ${env.BUILD_URL}",
                'shirsathamit2025@gmail.com'
            )
        }

        failure {
            sendEmail(
                "${env.JOB_NAME} - ${env.BUILD_NUMBER} - Build FAILED",
                "Build FAILED. Please check the console output at ${env.BUILD_URL}",
                'shirsathamit2025@gmail.com'
            )
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
