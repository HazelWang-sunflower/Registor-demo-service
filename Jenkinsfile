pipeline {
    agent any

    environment {
        ALIYU_REGION = 'us-west-2'
//         MIRROR_NAME = 'register-demo-service:v0.0.1'
        // Add other necessary environment variables here
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
                    echo 'Building..'
                    // Build the application
                    withMaven() {
                        sh 'mvn clean package'
                    }
                }
            }
        }

        stage('Unit Test') {
            steps {
                script {
                    // Run unit tests
                    echo 'Unit Test..'
                    sh 'make test'  // Modify this according to your test process
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
                sh "docker rm -f register-demo-service:v0.0.1 . "
            }
        }

        stage('Publish') {
            steps {
                script {
                    // Deploy application to Kubernetes cluster
//                     deployToK8s(env.BRANCH_NAME)
                    echo 'Publish to Kubernetes..'
                    sh "mvn clean package"
                    sh "docker build -t register-demo-service:v0.0.1 . "
                    // login to aliyu
                    // publish mirror
                }
            }
        }
    }

    post {
        always {
            cleanWs()  // Clean workspace after each build
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