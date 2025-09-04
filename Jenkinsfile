pipeline {
  agent any
  options { skipDefaultCheckout true; timestamps() }
  tools { jdk 'jdk17'; maven 'mvn3' } // noms = ceux configurés dans Manage Jenkins > Global Tool Configuration
  triggers { pollSCM('H/5 * * * *') } // ou webhook GitHub

  environment {
    // OPTION qualité (laisse vide si tu n'utilises pas Sonar maintenant)
    SONAR_HOST_URL = '' // ex: 'http://<ip-sonar>:9000'
    // OPTION artefacts (laisse vide si tu n'utilises pas Nexus maintenant)
    NEXUS_URL      = '' // ex: 'http://<ip-nexus>:8081/repository/maven-releases/'
  }

  stages {
    stage('Checkout') {
      steps {
        git branch: 'main', url: 'https://github.com/YoussefRahali/SmartConseil-Back.git'
      }
    }

    stage('Build & Test') {
      steps {
        sh 'mvn -U -B clean verify'   // compile + tests
      }
    }

    stage('SonarQube') {
      when { expression { return env.SONAR_HOST_URL?.trim() } }
      steps {
        withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
          sh '''
            mvn -B sonar:sonar \
              -Dsonar.host.url=$SONAR_HOST_URL \
              -Dsonar.login=$SONAR_TOKEN
          '''
        }
      }
    }

    stage('Publish to Nexus') {
      when { expression { return env.NEXUS_URL?.trim() } }
      steps {
        withCredentials([usernamePassword(credentialsId: 'NEXUS_CRED', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
          sh '''
            mkdir -p ~/.m2
            cat > ~/.m2/settings.xml <<'EOF'
            <settings><servers><server>
              <id>deploymentRepo</id>
              <username>${NEXUS_USER}</username>
              <password>${NEXUS_PASS}</password>
            </server></servers></settings>
EOF
            mvn -B -DskipTests deploy
          '''
        }
      }
    }
  }

  post {
    always {
      // multi-modules safe: archive tous les jars buildés
      archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
    }
  }
}
