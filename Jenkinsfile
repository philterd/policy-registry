pipeline {
    agent any
    tools {
        maven 'maven-3.6.3'
        jdk 'java-1.11.0-openjdk-amd64'
    }
    triggers {
        pollSCM 'H/10 * * * *'
    }
    options {
        buildDiscarder(logRotator(numToKeepStr: '30'))
    }
    parameters {
        booleanParam(defaultValue: false, description: 'Build and push Docker Image', name: 'isDocker')
        booleanParam(defaultValue: false, description: 'Build AMI', name: 'isAMI')
    }
    environment {
        //Use Pipeline Utility Steps plugin to read information from pom.xml into env variables
        IMAGE = readMavenPom().getArtifactId()
        VERSION = readMavenPom().getVersion()
    }
    stages {
        stage ('Initialize') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                '''
            }
        }
        stage ('Build') {
            steps {
                sh "mvn -version"
                sh "mvn -U license:aggregate-add-third-party license:aggregate-download-licenses install deploy"
                sh "./set-version.sh ${env.BUILD_NUMBER} ${env.VERSION}"
            		sh "./code-analysis.sh"
            }
        }
        stage ('Docker') {
            when {
                expression {
                    if (env.ISDOCKER == "true") {
                        return true
                    }
                    return false
                }
            }
            steps {
                sh './copy-to-distribution.sh'
                dir ('scripts/packaging/docker/') {
                    sh "./build-image.sh ${env.BUILD_NUMBER} ${env.VERSION}"
                    sh "./push-to-dockerhub.sh ${env.BUILD_NUMBER} ${env.VERSION}"
                    sh "./delete-image.sh ${env.BUILD_NUMBER} ${env.VERSION}"
                }
            }
        }
        stage ('AMI') {
            when {
                expression {
                    if (env.ISAMI == "true") {
                        return true
                    }
                    return false
                }
            }
            steps {
                sh './copy-to-distribution.sh'
                dir ('scripts/packaging/packer/') {
                    sh "./build-ami.sh ${env.BUILD_NUMBER} jenkins ${env.VERSION}"
                }
            }
        }
        stage ('Azure') {
            when {
                expression {
                    if (env.ISAZURE == "true") {
                        return true
                    }
                    return false
                }
            }
            steps {
                sh './copy-to-distribution.sh'
                dir ('scripts/packaging/packer/') {
                    sh "./build-azure.sh ${env.BUILD_NUMBER} ${env.VERSION}"
                }
            }
        }
    }
    post {
        success {
            sh "docker system prune -f"
            slackSend (color: '#00FF00', message: "SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
        }
        failure {
            slackSend (color: '#FF0000', message: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
            sh "docker system prune -f"
            mail to: 'jeff.zemerick@mtnfog.com',
                 subject: "Failed Pipeline: ${currentBuild.fullDisplayName}",
                 body: "Something is wrong with ${env.BUILD_URL}"
        }
    }
}
