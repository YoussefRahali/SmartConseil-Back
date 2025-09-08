pipeline {
  agent any

  options {
    timestamps()
    ansiColor('xterm')
    disableConcurrentBuilds()
    buildDiscarder(logRotator(numToKeepStr: '20'))
  }

  // Active le profil Spring "test" pour les @SpringBootTest (application-test.properties)
  environment {
    SPRING_PROFILES_ACTIVE = 'test'
    // Optionnel: si vous avez besoin de MAVEN_OPTS globaux
    MAVEN_OPTS = '-Xmx2g -Duser.timezone=UTC'
  }

  stages {

    stage('Prepare workspace (clean)') {
      steps {
        script {
          // Nettoyage robuste du workspace (avec fallback si cleanWs() n’est pas dispo)
          try {
            cleanWs(deleteDirs: true, disableDeferredWipeout: true, notFailBuild: true)
          } catch (ignored) {
            echo 'cleanWs() indisponible — utilisation de deleteDir()'
            deleteDir()
          }
        }
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
          # -Ptest est inoffensif s’il n’existe pas dans votre POM (Maven continue).
          mvn -B -U -V \
            -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} \
            -Ptest \
            clean verify
        '''
      }
    }

    stage('SonarQube') {
      when {
        // Exécute Sonar uniquement si le build a réussi
        expression { currentBuild.currentResult == null || currentBuild.currentResult == 'SUCCESS' }
      }
      steps {
        // Remplacez 'SonarQube' par le nom de votre serveur Sonar configuré dans Jenkins
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
        // Publie uniquement sur la branche main
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
      // Rapports de tests même en cas d’échec (pour voir les erreurs Surefire)
      junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'

      // Archive des artefacts si présents
      archiveArtifacts allowEmptyArchive: true, artifacts: '**/target/*.jar,**/target/*.war'

      // Repart d’un workspace propre pour le prochain run
      cleanWs(deleteDirs: true, disableDeferredWipeout: true, notFailBuild: true)
    }
  }
}
