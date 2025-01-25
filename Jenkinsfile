pipeline {
    agent any

    tools{
        maven 'Maven3.9.9'
    }

    environment {
        ALIYU_REGION = 'us-west-2'
//         MIRROR_NAME = 'register-demo-service:v0.0.1'
    }

    stages {
        stage('Checkout') {
            steps {
                // Checkout the code from the repository
                echo 'Checkout..'
                sh 'git clone https://github.com/HazelWang-sunflower/Registor-demo-service.git'
            }
        }

        stage('Build') {
            steps {
                script {
                    echo 'Building...'
                    // Build the application
                    withMaven() {
                        sh 'mvn -Dmaven.test.failure.ignore=true clean package'
                    }
                }
            }

            post {
                // If Maven was able to run the tests, even if some of the test
                // failed, record the test results and archive the jar file.
                success {
                    archiveArtifacts 'target/*.jar'
                }
            }
        }

        stage('Unit Test') {
            steps {
                script {
                    // Run unit tests
                    echo 'Unit Test..'
                }
            }
        }

        stage('Security Scan') {
            steps {
                script {
                    // Run security scans
                    echo 'Security Scan..'  // Modify this according to your security scanning tool
                }
            }
        }

        stage('Terraform Init') {
            steps {
                script {
                    // Initialize Terraform
                    echo 'Terraform init..'
                }
            }
        }

        stage('Terraform Plan') {
            steps {
                script {
                    // Plan Terraform changes based on the branch
                    echo 'Terraform Plan..'
                }
            }
        }

        stage('Terraform Apply') {
            steps {
                script {
                    // Apply Terraform changes based on the branch
                    echo 'Terraform Apply..'
                }
            }
        }

        stage('Clean'){
            steps{
//                 sh "docker rm -f app:v0.0.1 . "
                echo 'Clean..'
            }
        }

        stage('Publish') {
            steps {
                script {
                    // Deploy application to Kubernetes cluster
//                     deployToK8s(env.BRANCH_NAME)
                    echo 'Publish to Kubernetes..'
                    sh "mvn clean package"
                    sh "docker build -t app ."
                    // login to aliyu
                    // publish mirror
                }
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploy..'
            }
        }
    }

    post {
        success {
            script {
                    print('Success Build')
            }
        }
    }
}

def deployToK8s(environment) {
    withCredentials([string(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
        sh """
        kubectl apply -f k8s/${environment}/deployment.yaml
        kubectl apply -f k8s/${environment}/service.yaml
        """
    }
}