pipeline {
    agent any
    tools {
        'org.jenkinsci.plugins.docker.commons.tools.DockerTool' '18.09'
    }
    environment {
        DOCKER_REGISTRY = "crpi-embxeomgvcgz74wi.cn-qingdao.personal.cr.aliyuncs.com"
        // 镜像名称
        IMAGE_NAME = "/aliyu_hazel/aliyu_mirror_reg"
        // 镜像标签
        IMAGE_TAG = "${IMAGE_NAME}"
        // 阿里云镜像仓库的认证信息 ID
        DOCKER_CREDENTIALS_ID = '2c597f84-a9f1-4fa5-817d-3f9fff199bd0'
        // 容器名称
        CONTAINER_NAME = 'spring-boot-app'
        // 容器暴露的端口
        CONTAINER_PORT = '10086'
        // 宿主机映射的端口
        HOST_PORT = '8080'
    }
    stages {
        stage('Pull Docker Image') {
            steps {
                echo"====image crpi-embxeomgvcgz74wi.cn-qingdao.personal.cr.aliyuncs.com/aliyu_hazel/aliyu_mirror_reg:${IMAGE_TAG}"
                script {
                    docker.withRegistry("https://${DOCKER_REGISTRY}", DOCKER_CREDENTIALS_ID) {
                                    docker.image('alpine').inside {
                                                sh 'echo "Docker connection successful!"'
                                            }
//                         docker.image("crpi-embxeomgvcgz74wi.cn-qingdao.personal.cr.aliyuncs.com/aliyu_hazel/aliyu_mirror_reg:register_api_63").pull()
                    }
                }
            }
        }
        stage('Deploy') {
            steps {
                script{
                    docker.withRegistry("https://${DOCKER_REGISTRY}", DOCKER_CREDENTIALS_ID) {
                        try {
                            def existingContainer = docker.container("${CONTAINER_NAME}")
                            existingContainer.stop()
                            existingContainer.remove()
                        } catch (Exception e) {
                            echo "容器 ${CONTAINER_NAME} 不存在，无需删除"
                        }

                        // 运行新的容器
                        docker.image("${IMAGE_NAME}:${IMAGE_TAG}").run(
                            "--name ${CONTAINER_NAME} -p ${HOST_PORT}:${CONTAINER_PORT}"
                        )
                    }
                }
            }
        }
    }
}