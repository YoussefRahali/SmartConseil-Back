pipeline {
  agent any

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

    stage('Build & Test (profile=test)') {
      steps {
        sh '''
          # --- Forcer JDK 17 pour l’utilisateur jenkins ---
          # Adapte le chemin si différent : ls /usr/lib/jvm
          export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
          export PATH="$JAVA_HOME/bin:$PATH"

          echo "=== JAVA_HOME: $JAVA_HOME ==="
          java -version
          mvn -v

          mvn -B -U -V -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} clean verify
        '''
      }
    }

    stage('SonarQube') {
      when { expression { currentBuild.currentResult == null || currentBuild.currentResult == 'SUCCESS' } }
      steps {
        withSonarQubeEnv('SonarQube') {
          sh '''
            export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
            export PATH="$JAVA_HOME/bin:$PATH"
            mvn -B -DskipTests=true -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} sonar:sonar
          '''
        }
      }
    }

    stage('Quality Gate') {
      when { expression { currentBuild.currentResult == null || currentBuild.currentResult == 'SUCCESS' } }
      steps {
        timeout(time: 10, unit: 'MINUTES') {
          waitForQualityGate abortPipeline: true
        }
      }
    }

    stage('Publish to Nexus') {
      when { branch 'main' }
      steps {
        sh '''
          export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
          export PATH="$JAVA_HOME/bin:$PATH"
          mvn -B -DskipTests=true -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} deploy
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
