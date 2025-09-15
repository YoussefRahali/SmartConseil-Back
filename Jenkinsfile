pipeline {
  agent any

  options {
    timestamps()
    disableConcurrentBuilds()
    buildDiscarder(logRotator(numToKeepStr: '20'))
    skipDefaultCheckout(true)
  }

  parameters {
    booleanParam(name: 'PUSH_DOCKER', defaultValue: true, description: 'Construire & pousser les images Docker')
  }

  environment {
    // JDK 17
    JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
    PATH = "${env.JAVA_HOME}/bin:${env.PATH}"

    // Maven/Java
    SPRING_PROFILES_ACTIVE = 'test'
    MAVEN_OPTS = '-Xmx2g -Duser.timezone=UTC'

    // Docker Hub
    DOCKER_REGISTRY  = 'docker.io'
    DOCKER_NAMESPACE = 'youssef5025'
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
          ]],
          extensions: [[$class: 'LocalBranch', localBranch: 'main']]
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

    stage('Docker build & push') {
      when { expression { params.PUSH_DOCKER } }
      steps {
        withCredentials([usernamePassword(
          credentialsId: 'docker-registry-cred',
          usernameVariable: 'DOCKER_USERNAME',
          passwordVariable: 'DOCKER_PASSWORD'
        )]) {
          sh '''
            set -eu
            export DOCKER_BUILDKIT=1

            COMMIT="$(git rev-parse --short HEAD)"
            echo "Version: ${COMMIT}"

            # 1) Rectification
            echo "Build microserviceRectification..."
            docker build \
              --file microservices/microserviceRectification/Dockerfile \
              --tag ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-rectification:${COMMIT} \
              --tag ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-rectification:latest \
              microservices/microserviceRectification

            # 2) Conseil
            echo "Build microserviceConseil..."
            docker build \
              --file microservices/microserviceConseil/Dockerfile \
              --tag ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-conseil:${COMMIT} \
              --tag ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-conseil:latest \
              microservices/microserviceConseil

            # 3) Rapport
            echo "Build microserviceRapport..."
            docker build \
              --file microservices/microserviceRapport/Dockerfile \
              --tag ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-rapport:${COMMIT} \
              --tag ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-rapport:latest \
              microservices/microserviceRapport

            echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin ${DOCKER_REGISTRY}

            docker push ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-rectification:${COMMIT}
            docker push ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-rectification:latest
            docker push ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-conseil:${COMMIT}
            docker push ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-conseil:latest
            docker push ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-rapport:${COMMIT}
            docker push ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-rapport:latest
          '''
        }
      }
    }

    stage('Diag Docker on agent') {
      steps {
        sh '''
          set -eux
          docker version
          docker info
          id
          groups
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
