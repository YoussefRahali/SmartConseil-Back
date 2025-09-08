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
      steps {
        // Nettoyage complet du workspace avant de commencer
        deleteDir()
      }
    }

    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build & Test (profile=test)') {
      steps {
        sh '''
          echo "=== Using Spring profile: ${SPRING_PROFILES_ACTIVE} ==="
          # -Ptest est optionnel : s’il n’existe pas dans le POM, Maven continue quand même.
          mvn -B -U -V \
            -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} \
            -Ptest \
            clean verify
        '''
      }
    }

    stage('SonarQube') {
      when {
        expression { currentBuild.currentResult == null || currentBuild.currentResult == 'SUCCESS' }
      }
      steps {
        // Remplacer le nom ci-dessous par celui configuré dans Jenkins si différent
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
      when {
        branch 'main'
      }
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
      // Rapports de tests & artefacts même en cas d’échec
      junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
      archiveArtifacts allowEmptyArchive: true, artifacts: '**/target/*.jar,**/target/*.war'

    
::contentReference[oaicite:0]{index=0}
