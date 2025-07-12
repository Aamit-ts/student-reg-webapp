node {
    def Maven_Home = tool name: 'Maven_3.9.10', type: 'maven'

    try {
        stage("Git Clone") {
            git branch: 'New_Branch', credentialsId: '893d49a3-02b3-4657-b8b9-865a98636a90', url: 'https://github.com/Aamit-ts/student-reg-webapp.git'
        }

        stage("Maven Build") {
            sh "${Maven_Home}/bin/mvn clean verify sonar:sonar"
        }

        stage("Maven Deploy") {
            sh "${Maven_Home}/bin/mvn clean deploy"
        }

        stage("Stop Tomcat Server") {
            sshagent(['tomcat_1']) {
                sh "echo Stopping tomcat server"
                sh "ssh -o StrictHostKeyChecking=no ec2-user@51.21.171.100 'sudo /opt/tomcat/bin/shutdown.sh'"
                sh "sleep 20"
            }
        }

        stage("Deploy Package") {
            sshagent(['tomcat_1']) {
                sh "scp -o StrictHostKeyChecking=no target/student-reg-webapp.war ec2-user@51.21.171.100:/opt/tomcat/webapps/student-reg-webapp.war"
            }
        }

        stage("Start Tomcat Server") {
            sshagent(['tomcat_1']) {
                sh "echo Starting tomcat server"
                sh "ssh -o StrictHostKeyChecking=no ec2-user@51.21.171.100 'sudo /opt/tomcat/bin/startup.sh'"
                sh "sleep 20"
            }
        }

    } catch (err) {
        echo "An error occurred: ${err.getMessage()}"
        currentBuild.result = 'FAILURE'

    } finally {
        def buildStatus = currentBuild.result ?: 'SUCCESS'
        def colorcode = buildStatus == 'SUCCESS' ? 'good' : 'danger'

        // Uncomment only if Slack plugin is installed
        // slackSend(channel: 'lic-appteam', color: "${colorcode}", message: "Build - ${buildStatus} : ${env.JOB_NAME} #${env.BUILD_NUMBER} - URL: ${env.BUILD_URL}")

        sendEmail(
            "${env.JOB_NAME} - ${env.BUILD_NUMBER} - Build ${buildStatus}",
            "Build ${buildStatus}. Please check the console output at ${env.BUILD_URL}",
            'shirsathamit2025@gmail.com'
        )
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
