pipeline {
    agent any
    stages {
        stage('Pull Docker Image') {
            steps {
            sh 'echo $PATH'


//                 sh "docker info"
//
//                 sh "docker pull crpi-embxeomgvcgz74wi.cn-qingdao.personal.cr.aliyuncs.com/aliyu_hazel/aliyu_mirror_reg:${IMAGE_NAME}"
            }
        }
        stage('Deploy') {
            steps {
//                 sh "docker run -u root --name test -d -p 10086:8888 ${IMAGE_NAME}"
            }
        }
    }
}