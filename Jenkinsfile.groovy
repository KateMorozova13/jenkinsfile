node {
   stage('Preparation') { 
      git 'https://github.com/KateMorozova13/gradle.git'
   }
def gradleHome = tool 'Gradle-5.1'
   stage('Building code') {
 sh "'${gradleHome}' clean compileJava "
   }
  stage('Testing code') {
       parallel(
      firstBranch: {
    sleep 1
      },
      secondBranch: {
      sleep 2
      },
      thirdBranch: {
       sleep 3
      }
    )
      }
          stage('Triggering child job') {
     build job: 'est'
      }
        stage('Packaging and Publishing results') {
    archiveArtifacts '**/*.jar'
    sh 'tar -czvf pipeline-$BUILD_NUMBER.tar.gz $JENKINS_HOME/workspace/kk/gradle/wrapper/*.jar'
         nexusArtifactUploader artifacts: [[artifactId: 'id', classifier: 'file', file: '$JENKINS_HOME/workspace/kk/pipeline-$BUILD_NUMBER.tar.gz', type: 'tar.gz']], credentialsId: 'e3f76f60-5789-43f9-aec3-566f0f8412c6', groupId: 'groupid', nexusUrl: '172.23.11.31:8081', nexusVersion: 'nexus3', protocol: 'http', repository: 'Deploy', version: '1.0'
      }
             stage('Asking for manual approval') {
 timeout(time: 2, unit: "MINUTES") {
    input message: 'Approve Deploy?', ok: 'Yes'}
}
    stage('Deploy') {
    
        sh 'wget http://172.23.11.31:8081/repository/Deploy/groupid/id/1.0/id-1.0-file.tar.gz'
    //sh 'tar -xf id-1.0-file.tar.gz'
    }
 stage('Email notifications') { 
 if ( currentBuild.result == 'SUCCESS') { 
 emailext body: 'Finished: Success', subject: 'The result of job', to: 'k.marozava@godeltech.com'
 
 } else { 
emailext body: 'Finished: Failed on stage: "${env.STAGE_NAME}"', subject: 'The result of job', to: 'k.marozava@godeltech.com'
 } 
 }
    }