def call(String serverIp, String warFile, String credentialsId) {
    sshagent([credentialsId]) {
        sh """
            ssh -o StrictHostKeyChecking=no ec2-user@${serverIp} '
                sudo rm -rf /opt/tomcat/webapps/student-reg-webapp &&
                sudo rm -f /opt/tomcat/webapps/student-reg-webapp.war
            '
            scp -o StrictHostKeyChecking=no ${warFile} ec2-user@${serverIp}:/opt/tomcat/webapps/student-reg-webapp.war
        """
    }
}
