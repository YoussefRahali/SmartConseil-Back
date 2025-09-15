pipeline {
  agent any

  options {
    timestamps()
    disableConcurrentBuilds()
    buildDiscarder(logRotator(numToKeepStr: '20'))
    skipDefaultCheckout(true)
  }

  parameters {
    booleanParam(name: 'DEPLOY_TO_NEXUS', defaultValue: false, description: 'Déployer sur Nexus')
    booleanParam(name: 'PUSH_DOCKER',     defaultValue: true,  description: 'Construire & pousser les images Docker')
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

    // Détection de branche (sera fixé dans le stage "Detect branch")
    ON_MAIN = 'false'
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
          extensions: [
            [$class: 'LocalBranch', localBranch: 'main']
          ]
        ])
      }
    }

    stage('Detect branch') {
      steps {
        script {
          def envBranch = env.GIT_BRANCH ?: ''
          def mbBranch  = env.BRANCH_NAME ?: ''
          def cur = sh(script: "git rev-parse --abbrev-ref HEAD || true", returnStdout: true).trim()
          echo "DEBUG :: GIT_BRANCH='${envBranch}' BRANCH_NAME='${mbBranch}' CURRENT='${cur}'"

          def isMain = (
            mbBranch == 'main' ||
            envBranch == 'main' || envBranch == 'origin/main' || envBranch.endsWith('/main') ||
            cur == 'main'
          )
          env.ON_MAIN = isMain ? 'true' : 'false'
          echo "ON_MAIN=${env.ON_MAIN}"
        }
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
  when {
       expression { params.PUSH_DOCKER }
  }
  steps {
    withCredentials([usernamePassword(
      credentialsId: 'docker-registry-cred',
      usernameVariable: 'DOCKER_USERNAME',
      passwordVariable: 'DOCKER_PASSWORD'
    )]) {
      sh '''
        set -eu
        export DOCKER_BUILDKIT=1
        VERSION="$(git rev-parse --short HEAD)"
        echo "Version: ${VERSION}"

        echo "Build microserviceRectification..."
        docker build -f microservices/microserviceRectification/Dockerfile \
          -t ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-rectification:${VERSION} \
          -t ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-rectification:latest .

        echo "Build microserviceConseil..."
        docker build -f microservices/microserviceConseil/Dockerfile \
          -t ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-conseil:${VERSION} \
          -t ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-conseil:latest .

        echo "Build microserviceRapport..."
        docker build -f microservices/microserviceRapport/Dockerfile \
          -t ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-rapport:${VERSION} \
          -t ${DOCKER_REGISTRY}/${DOCKER_NAMESPACE}/microservice-rapport:latest .

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
