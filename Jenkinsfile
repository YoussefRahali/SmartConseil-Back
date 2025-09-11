pipeline {
  agent any

  options {
    timestamps()
    disableConcurrentBuilds()
    buildDiscarder(logRotator(numToKeepStr: '20'))
    skipDefaultCheckout(true)
  }

  environment {
    // JDK 17 de ta VM
    JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
    PATH = "${env.JAVA_HOME}/bin:${env.PATH}"

    // Maven/Java
    SPRING_PROFILES_ACTIVE = 'test'
    MAVEN_OPTS = '-Xmx2g -Duser.timezone=UTC'

    // Registre d'images (si Docker Hub : docker.io) + namespace (ton username Docker Hub)
    DOCKER_REGISTRY  = 'docker.io'
    DOCKER_NAMESPACE = 'youssef5025'   // <-- mets ton vrai username Docker Hub
  }

  stages {

    stage('Prepare workspace (clean)') {
      steps { deleteDir() }
    }

    stage('Checkout') {
  steps {
    checkout([
      $class: 'GitSCM',
      branches: [[name: '*/main']],
      userRemoteConfigs: [[
        url: 'https://github.com/YoussefRahali/SmartConseil-Back.git',
        credentialsId: 'github-cred'
      ]]
    ])
  }
}


    stage('Build & Test (profile=test)') {
      steps {
        sh '''
          set -eu
          echo "=== JAVA_HOME: $JAVA_HOME ==="
          java -version
          mvn -v

          echo "=== Using Spring profile: ${SPRING_PROFILES_ACTIVE} ==="
          mvn -B -U -V -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} clean verify
        '''
      }
    }

    stage('SonarQube') {
      when { expression { currentBuild.currentResult == null || currentBuild.currentResult == 'SUCCESS' } }
      steps {
        // Le libellé ci-dessous doit correspondre à Manage Jenkins > System > SonarQube servers
        withSonarQubeEnv('SonarQube') {
          sh '''
            set -eu
            mvn -B -DskipTests \
              -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} \
              org.sonarsource.scanner.maven:sonar-maven-plugin:4.0.0.4121:sonar
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
          set -eu
          mvn -B -DskipTests -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} deploy
        '''
      }
    }

    stage('Docker build & push (main)') {
      when { branch 'main' }
      steps {
        withCredentials([usernamePassword(
          credentialsId: 'docker-registry-cred',
          usernameVariable: 'DOCKER_USERNAME',
          passwordVariable: 'DOCKER_PASSWORD'
        )]) {
          sh '''
            set -eu

            # Tag version basé sur le commit
            VERSION="$(git rev-parse --short HEAD)"

            echo "Docker build microserviceRectification..."
            docker build -f microservices/microserviceRectification/Dockerfile \
              -t ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-rectification:${VERSION} \
              -t ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-rectification:latest .

            echo "Docker build microserviceConseil..."
            docker build -f microservices/microserviceConseil/Dockerfile \
              -t ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-conseil:${VERSION} \
              -t ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-conseil:latest .

            echo "Docker build microserviceRapport..."
            docker build -f microservices/microserviceRapport/Dockerfile \
              -t ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-rapport:${VERSION} \
              -t ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-rapport:latest .

            echo "Docker login & push..."
            echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin ${DOCKER_REGISTRY}

            docker push ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-rectification:${VERSION}
            docker push ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-rectification:latest

            docker push ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-conseil:${VERSION}
            docker push ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-conseil:latest

            docker push ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-rapport:${VERSION}
            docker push ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-rapport:latest
          '''
        }
      }
    }
  }

  post {
    always {
      junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
      archiveArtifacts allowEmptyArchive: true, artifacts: '**/target/*.jar,**/target/*.war'

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
