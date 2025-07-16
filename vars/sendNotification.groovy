def call(String status, String recipient) {
    def subject = "${env.JOB_NAME} - ${env.BUILD_NUMBER} - Build ${status}"
    def body = "Build ${status}. Please check the console output at ${env.BUILD_URL}"
    emailext(subject: subject, body: body, to: recipient, mimeType: 'text/html')
}
