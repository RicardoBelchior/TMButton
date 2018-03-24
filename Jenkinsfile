pipeline {
  agent {
    docker {
      image 'circleci/android:api-25-node8-alpha'
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
    stage('UI Test') {
      steps {
        sh 'sh start_emulator.sh'
        sh './gradlew connectedAndroidTest'
        sh 'sh stop_emulator.sh'
      }
    }
  }
}