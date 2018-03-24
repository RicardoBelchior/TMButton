pipeline {
  agent {
    docker {
      image 'runmymind/docker-android-sdk:ubuntu-standalone'
    }
    
  }
  stages {
    stage('Compile') {
      steps {
        sh './gradlew compileDebugSources'
      }
    }
    stage('Unit Test') {
      steps {
        sh './gradlew testDebugUnitTest'
        junit '**/TEST-*.xml'
      }
    }
    stage('Build Demo APK') {
      steps {
        sh './gradlew assembleDebug'
        archiveArtifacts '**/*.apk'
      }
    }
    stage('Static analysis') {
      steps {
        sh './gradlew lintDebug'
        androidLint(pattern: '**/lint-results-*.xml')
      }
    }
  }
}