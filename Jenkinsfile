pipeline {
    agent any
    environment {
        DOCKER_REGISTRY = "crpi-embxeomgvcgz74wi.cn-qingdao.personal.cr.aliyuncs.com"
        IMAGE_NAME = "/aliyu_hazel/aliyu_mirror_reg"
        IMAGE_TAG = "${IMAGE_NAME}"
        DOCKER_CREDENTIALS_ID = '2c597f84-a9f1-4fa5-817d-3f9fff199bd0'
    }
    stages {
        stage('Pull Docker Image') {
            steps {
                docker.withRegistry("https://${DOCKER_REGISTRY}", DOCKER_CREDENTIALS_ID) {
                    docker.image("${IMAGE_NAME}:${IMAGE_TAG}").pull()
                }
            }
        }
        stage('Deploy') {
            steps {
                def customImage = docker.build("${IMAGE_NAME}:${IMAGE_TAG}", '')
                docker.withRegistry("https://${DOCKER_REGISTRY}", DOCKER_CREDENTIALS_ID) {
                    customImage.push()
                }
            }
        }
    }
}