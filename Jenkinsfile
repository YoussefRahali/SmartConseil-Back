pipeline {
  agent any

  // Si, dans "Global Tool Configuration", tu as nommé le JDK "JAVA_HOME" et Maven "Maven",
  // Jenkins mettra automatiquement le bon JAVA_HOME/PATH et mvn dans le PATH.
  tools {
    jdk   'JAVA_HOME'      // nom exact que tu as mis côté Jenkins (voir capture)
    maven 'Maven'          // adapte si tu l'as nommé autrement
  }

  options {
    timestamps()
    disableConcurrentBuilds()
    buildDiscarder(logRotator(numToKeepStr: '20'))
    skipDefaultCheckout(true)
  }

  environment {
    // Sécurise aussi au cas où : on force le JDK 17 de ta VM
    JAVA_HOME = '/usr/lib/jvm/default-java'
    PATH = "${JAVA_HOME}/bin:${PATH}"

    SPRING_PROFILES_ACTIVE = 'test'
    MAVEN_OPTS = '-Xmx2g -Duser.timezone=UTC'

    // ---- Docker (optionnel) ----
    // Pour Docker Hub : DOCKER_REGISTRY='docker.io' et DOCKER_NAMESPACE='<ton-username>'
    DOCKER_REGISTRY   = 'docker.io'           // à adapter si registre privé
    DOCKER_NAMESPACE  = 'mon-espace'          // ex: ton username DockerHub
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
          set -eux
          echo "=== Using Spring profile: ${SPRING_PROFILES_ACTIVE} ==="
          mvn -B -U -V \
            -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} \
            clean verify
        '''
      }
    }

    stage('SonarQube') {
      when { expression { currentBuild.currentResult == null || currentBuild.currentResult == 'SUCCESS' } }
      steps {
        withSonarQubeEnv('SonarQube') {           // nom du serveur Sonar déclaré dans Jenkins
          sh '''
            set -eux
            mvn -B -DskipTests=true \
              -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} \
              sonar:sonar
          '''
        }
      }
    }

    stage('Quality Gate') {
      when { expression { currentBuild.currentResult == null || currentBuild.currentResult == 'SUCCESS' } }
      steps {
        timeout(time: 5, unit: 'MINUTES') {
          waitForQualityGate abortPipeline: true   // stoppe le build si la gate est KO
        }
      }
    }

    stage('Publish to Nexus') {
      when { branch 'main' }
      steps {
        sh '''
          set -eux
          mvn -B -DskipTests=true \
            -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} \
            deploy
        '''
      }
    }

    stage('Docker build & push (optionnel)') {
      when { branch 'main' }
      steps {
        withCredentials([usernamePassword(
          credentialsId: 'docker-registry-cred',
          usernameVariable: 'REG_USER',
          passwordVariable: 'REG_PASS'
        )]) {
          sh '''
            set -eux
            echo "$REG_PASS" | docker login -u "$REG_USER" --password-stdin ${DOCKER_REGISTRY}

            # microserviceConseil
            docker build -t ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microserviceconseil:${BUILD_NUMBER} \
              -f microservices/microserviceConseil/Dockerfile .
            docker push ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microserviceconseil:${BUILD_NUMBER}

            # Rectification (adapte le chemin si besoin)
            docker build -t ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/rectification:${BUILD_NUMBER} \
              -f microservices/rectification/Dockerfile .
            docker push ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/rectification:${BUILD_NUMBER}
          '''
        }
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
