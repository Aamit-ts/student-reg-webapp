def call(String sonarSecret) {
    sh "mvn clean package sonar:sonar -Dsonar.login=${sonarSecret}"
}

