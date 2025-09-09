pipeline {
  agent any

  tools {
    // Noms EXACTS tels que définis dans "Global Tool Configuration"
    jdk   'JDK17'
    maven 'Maven'      // mets ici le nom de ton Maven tool
  }

  options {
    timestamps()
    disableConcurrentBuilds()
    buildDiscarder(logRotator(numToKeepStr: '20'))
    skipDefaultCheckout(true)
  }

  environment {
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
          echo "=== Using Spring profile: ${SPRING_PROFILES_ACTIVE} ==="
          mvn -B -U -V \
            -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} \
            clean verify
        '''
      }
    }

    stage('SonarQube analysis') {
      when { expression { currentBuild.currentResult == null || currentBuild.currentResult == 'SUCCESS' } }
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

    stage('Quality Gate') {
      steps {
        timeout(time: 10, unit: 'MINUTES') {
          waitForQualityGate() // nécessite le webhook Sonar -> Jenkins OK
        }
      }
    }

    stage('Publish to Nexus') {
      when { branch 'main' }
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
