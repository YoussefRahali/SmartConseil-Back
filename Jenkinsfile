pipeline {
  agent any

  options {
    timestamps()
    disableConcurrentBuilds()
    buildDiscarder(logRotator(numToKeepStr: '20'))
    skipDefaultCheckout(true)
  }

  environment {
    // JDK 17 exact de ta VM
    JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
    PATH = "${env.JAVA_HOME}/bin:${env.PATH}"

    // Maven/Java options
    SPRING_PROFILES_ACTIVE = 'test'
    MAVEN_OPTS = '-Xmx2g -Duser.timezone=UTC'

    // Si tu pousses des images (facultatif)
    DOCKER_REGISTRY = 'docker.io'        // ou registre privé
    DOCKER_NAMESPACE = 'ton-username'    // adapte si besoin
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
          echo "=== JAVA_HOME: $JAVA_HOME ==="
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
            mvn -B -DskipTests \
              -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} \
              sonar:sonar
          '''
        }
      }
    }

    stage('Quality Gate') {
      when {
        expression { currentBuild.currentResult == null || currentBuild.currentResult == 'SUCCESS' }
      }
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
          mvn -B -DskipTests \
            -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} \
            deploy
        '''
      }
    }

    // (Optionnel) Build & push Docker s’il y a des Dockerfile
    stage('Docker build & push (main)') {
      when { branch 'main' }
      steps {
        sh '''
          echo "Docker build microserviceRectification..."
          docker build -t ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-rectification:latest \
            -f microservices/microserviceRectification/Dockerfile .

          echo "Docker build microserviceConseil..."
          docker build -t ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-conseil:latest \
            -f microservices/microserviceConseil/Dockerfile .

          echo "Docker build microserviceRapport..."
          docker build -t ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-rapport:latest \
            -f microservices/microserviceRapport/Dockerfile .

          echo "Docker login & push..."
          echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin ${DOCKER_REGISTRY}
          docker push ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-rectification:latest
          docker push ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-conseil:latest
          docker push ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-rapport:latest
        '''
      }
    }
  }

  post {
    always {
      // Rapports/tests et jars
      junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
      archiveArtifacts allowEmptyArchive: true, artifacts: '**/target/*.jar,**/target/*.war'

      // Aide au diagnostic si un test replante dans Jenkins
      script {
        if (currentBuild.currentResult == 'FAILURE') {
          sh '''
            echo "=== DIAG: lister les rapports Surefire du module Rapport ==="
            if [ -d microservices/microserviceRapport/target/surefire-reports ]; then
              ls -lah microservices/microserviceRapport/target/surefire-reports || true
              echo "=== Contenu des .txt (premières lignes) ==="
              for f in microservices/microserviceRapport/target/surefire-reports/*.txt; do
                echo "---- $f ----"; head -n 80 "$f" || true; echo;
              done
            else
              echo "Pas de dossier surefire-reports pour microserviceRapport"
            fi
          '''
        }
      }

      deleteDir()
    }
  }
}
