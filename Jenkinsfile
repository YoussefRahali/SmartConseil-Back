pipeline {
  agent any

  tools {
    // (Facultatif) si tu as déclaré un Maven dans "Global Tool Configuration"
    // maven 'Maven-3.9'
  }

  options {
    timestamps()
    disableConcurrentBuilds()
    buildDiscarder(logRotator(numToKeepStr: '20'))
    skipDefaultCheckout(true)
  }

  environment {
    // Ton JDK 17 réel d'après ta VM
    JAVA_HOME = '/usr/lib/jvm/default-java'
    PATH = "${JAVA_HOME}/bin:${PATH}"

    // Maven/Java
    SPRING_PROFILES_ACTIVE = 'test'
    MAVEN_OPTS = '-Xmx2g -Duser.timezone=UTC'
  }

  stages {

    stage('Prepare workspace (clean)') {
      steps { deleteDir() }
    }

    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Build & Test') {
      steps {
        sh '''
          java -version
          mvn -v
          echo "=== Using Spring profile: ${SPRING_PROFILES_ACTIVE} ==="
          mvn -B -U -V \
            -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} \
            clean verify
        '''
      }
    }

    stage('SonarQube') {
      when {
        expression { currentBuild.currentResult == null || currentBuild.currentResult == 'SUCCESS' }
      }
      steps {
        withSonarQubeEnv('SonarQube') {
          sh '''
            mvn -B -DskipTests=true \
              -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} \
              sonar:sonar
          '''
        }
      }
    }

    stage('Publish to Nexus') {
      when { allOf { branch 'main'; expression { fileExists('pom.xml') } } }
      steps {
        sh '''
          mvn -B -DskipTests=true \
            -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} \
            deploy
        '''
      }
    }
  }

  post {
    always {
      junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
      archiveArtifacts allowEmptyArchive: true, artifacts: '**/target/*.jar,**/target/*.war'
      deleteDir()
    }
  }
}
