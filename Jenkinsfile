pipeline {
  agent any

  options {
    timestamps()
    disableConcurrentBuilds()
    buildDiscarder(logRotator(numToKeepStr: '20'))
    skipDefaultCheckout(true)
  }

  // On force le JDK 17 système et on le met en tête du PATH
  environment {
    JAVA_HOME = '/usr/lib/jvm/default-java'
    PATH = "${JAVA_HOME}/bin:${env.PATH}"

    SPRING_PROFILES_ACTIVE = 'test'
    MAVEN_OPTS = '-Xmx2g -Duser.timezone=UTC'
  }

  stages {
    stage('Prepare workspace (clean)') {
      steps {
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
          echo "=== Using Java at: $JAVA_HOME ==="
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
            mvn -B -DskipTests=true \
              -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} \
              sonar:sonar
          '''
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

    // --- (Optionnel) Docker : dé-commente si prêt à builder/pusher ---
    // stage('Docker build & push') {
    //   when { branch 'main' }
    //   environment {
    //     REGISTRY_CRED = 'docker-registry-cred'   // ID des Credentials Jenkins
    //     IMAGE_NAME    = 'monorg/microservice-conseil' // adapte le nom
    //   }
    //   steps {
    //     sh 'docker version'
    //     script {
    //       docker.withRegistry('', REGISTRY_CRED) {
    //         def img = docker.build("${IMAGE_NAME}:${env.BUILD_NUMBER}")
    //         img.push()
    //         img.push('latest')
    //       }
    //     }
    //   }
    // }
  }

  post {
    always {
      junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
      archiveArtifacts allowEmptyArchive: true, artifacts: '**/target/*.jar,**/target/*.war'
      deleteDir()
    }
  }
}
