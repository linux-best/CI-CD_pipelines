pipeline {
    agent any
    
    environment {
        NAME = "amirmahdi"
        LAST_NAME = 'fathipoor'
        VALUE = 1
        result_ISP = sh (script:'ping -c 4 8.8.8.8', returnStdout: true).trim()
        result_server = sh (script:'ping -c 4 192.168.1.8', returnStdout: true).trim()
    }
    
    stages {
        stage ("Checkout SCM") {
            steps {
                git branch: 'main', url: 'https://github.com/linux-best/scripts/'
                
            }
        }
        stage ("Command_Execution") {
            steps {
                sh "ls -lha"
            }
            
        }
        stage ("Build") {
            when {
                expression {
                    env.VALUE != 1
                }
            }
            steps {
                echo "Hi"
                echo "My name is ${env.NAME}"
                echo "My last name is ${env.LAST_NAME}"
            }
        }
        
        stage ("test") {
            steps {
                echo "test server : ${env.result_server}"
                echo "test ISP : ${env.result_ISP}"
            }
        }
    }
    
        
    post {
        success {
            echo "Done !"
            script {
                emailext(
                    subject: "Build ${env.BUILD_NUMBER} of ${env.JOB_NAME} was successful",
                    body: """
                    Check the detals at ${env.BUILD_URL}""",
                    to: "amirmahdifhp@gmail.com"
                )
            }
        } // email
        failure {
            echo "Fuck !!"
            script {
                emailext(
                    subject: "Build ${env.BUILD_NUMBER} of ${env.JOB_NAME} was failed",
                    body: """
                    Check the details at ${env.BUILD_URL}""",
                    to: "amirmahdifhp@gmail.com"
                )
            }
        } // email
    }
}
