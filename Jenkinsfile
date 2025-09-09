pipeline {
  agent any

  // ❌ pas de bloc "tools" pour éviter l'erreur "No tools specified"
  // On suppose que Maven est déjà installé et accessible via "mvn".
  // Si besoin, on peut forcer PATH plus bas.

  options {
    timestamps()
    disableConcurrentBuilds()
    buildDiscarder(logRotator(numToKeepStr: '20'))
    skipDefaultCheckout(true)
  }

  environment {
    // JDK réel de ta VM (tu l’as confirmé)
    JAVA_HOME = '/usr/lib/jvm/default-java'
    PATH = "${JAVA_HOME}/bin:${PATH}"

    SPRING_PROFILES_ACTIVE = 'test'
    MAVEN_OPTS = '-Xmx2g -Duser.timezone=UTC'

    // Optionnel si tu pushes sur Docker Hub :
    // DOCKER_REGISTRY = 'docker.io'
    // DOCKER_NAMESPACE = '<ton-user-dockerhub>'
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
          echo "=== Using JAVA_HOME: $JAVA_HOME ==="
          echo "=== Using Spring profile: ${SPRING_PROFILES_ACTIVE} ==="
          mvn -v
          mvn -B -U -V -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} clean verify
        '''
      }
    }

    stage('SonarQube') {
      when { expression { currentBuild.currentResult == null || currentBuild.currentResult == 'SUCCESS' } }
      steps {
        // Le nom "SonarQube" doit correspondre à Manage Jenkins → System → SonarQube servers
        withSonarQubeEnv('SonarQube') {
          sh '''
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
