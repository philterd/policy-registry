pipeline {
    agent any
    tools {
        maven 'maven-3.6.0'
        jdk 'amazon-corretto-11'
    }
    options {
        buildDiscarder(logRotator(numToKeepStr: '30'))
    }
    parameters {
        booleanParam(defaultValue: true, description: 'Build Docker and push to ECR', name: 'isDocker')
        booleanParam(defaultValue: false, description: 'Build AMI', name: 'isAMI')
        booleanParam(defaultValue: false, description: 'Build Azure VHD', name: 'isAzure')
        booleanParam(defaultValue: true, description: 'Source Analysis', name: 'isAnalysis')
    }
    environment {
        //Use Pipeline Utility Steps plugin to read information from pom.xml into env variables
        IMAGE = readMavenPom().getArtifactId()
        VERSION = readMavenPom().getVersion()
        PHILTER_INDEX_DIR = "${WORKSPACE}"
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
                sh "mvn license:aggregate-add-third-party license:aggregate-download-licenses install deploy -Dmaven.javadoc.skip=true"
                sh "./set-version.sh ${env.BUILD_NUMBER} ${env.VERSION}"
            }
        }
        stage ('Analysis') {
            when {
                expression {
                    if (env.ISANALYSIS == "true") {
                        return true
                    }
                    return false
                }
            }
            steps {
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
                    sh "./push-to-aws.sh ${env.BUILD_NUMBER} ${env.VERSION}"
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
        }
        failure {
            mail to: 'jeff.zemerick@mtnfog.com',
                 subject: "Failed Pipeline: ${currentBuild.fullDisplayName}",
                 body: "Something is wrong with ${env.BUILD_URL}"
        }
    }
}
