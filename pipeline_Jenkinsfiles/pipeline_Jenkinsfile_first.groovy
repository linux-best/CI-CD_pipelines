pipeline {
    agent any

    tools {
        // sonarqube
    }
    environment {
        PATH_TO_BACKUP_1 = '/var/lib/jenkins/repo_projects/ansible'
        PATH_TO_BACKUP_2 = '/var/lib/jenkins/repo_projects/scripts'
        GIT_CODE_CHANGE = false
        ANSIBLE_DEPLOY_YML = 'playbook_Deploy.yml'
        UPDATE_SYSTEM_SH = 'update_system.sh'
        BACKUP_SH = 'Backup.sh'
        REPO_SCRIPTS = 'https://github.com/linux-best/scripts/'
        REPO_ANSINLE = 'https://github.com/linux-best/ansible/'
    }

    stages {
        stage ("Checkout Scm") {
            parallel {
                stage ('Task A') {
                    steps {
                        echo "Clonning "
                        dir("repo_scripts") {
                            git url:${env.REPO_SCRIPTS}, branch:'main'
                        }
                    }    
                }
                stage ('Task B') {
                    steps {
                        echo ""
                        dir("repo_ansible") {
                            git url:${env.REPO_ANSINLE}, branch:'main'    
                        }
                    }    
                }
            }
        }
        stage ("Code Analysis") {   
            parallel {
                stage ('Task A') {
                    steps {
                        dir("repo_scripts") {
                            sh "git fetch origin main"
                            script {
                                def scripts_change = sh(script:'git diff --quit origin/main...HEAD',returnStatus: true)
                                if (scripts_change == 1) {
                                    def script_change_list = sh(script:'git diff --name-only',returnStdout: true).trim()
                                    echo "list of changed files : ${script_change_list}"
                                    ${env.GIT_CODE_CHANGE} = true
                                    echo "Code change value = ${env.GIT_CODE_CHANGE}"
                                    sh "sonar_scanner"
                                    // sh "yara_command"
                                } else {
                                    ${env.GIT_CODE_CHANGE} = false
                                    echo "Code change value = ${env.GIT_CODE_CHANGE}"
                                }
                            }
                        }
                    }
                }   
                stage ('Task B') {
                    steps {
                        dir("repo_ansbile") {
                            sh "git fetch origin main"
                            script {
                                def ansible_change = sh(script:'git diff --quit origin/main...HEAD',returnStatus: true)
                                if (ansible_change == 1) {
                                    def ansible_change_list = sh(script:'git diff --name-only',returnStdout: true).trim()
                                    echo "list of changed files : ${ansible_change_list}"
                                    ${env.GIT_CODE_CHANGE} = true
                                    echo "Code change value = ${env.GIT_CODE_CHANGE}"
                                    sh "sonar_scanner"
                                    // sh "yara_command"
                                } else {
                                    ${env.GIT_CODE_CHANGE} = false
                                    echo "Code change value = ${env.GIT_CODE_CHANGE}"
                                }
                            }                    
                        }
                    }    
                }
            }
        }
        stage ("Build") {
            steps {
                echo "Building stage ....."
                dir("repo_scripts") {
                    sh "./${UPDATE_SYSTEM_SH}"
                    sh "pip install -r requirments.txt"
                }
            }
        }
        stage ("Test") {
            parallel {
                stage ('Task A') {
                    dir('repo_scripts') {
                        sh "./${BACKUP_SH} ${env.PATH_TO_BACKUP_1}"
                        sh "./${BACKUP_SH} ${env.PATH_TO_BACKUP_2}"
                    }
                }
                stage ('Task B') {
                    dir('repo_scripts') {
                        script {
                            def value = sh(script:'python3 health_check.py',returnStdout: true).trim()
                            if (value == 0) {
                                echo "connection is ok !"
                            } else {
                                error("connection lost !")
                            }
                        }
                    }
                }
            }
        }
        stage ('Deploy') {
            steps {
                echo "Deploying application .... "
                dir("repo_ansible") {
                    sh "ansible_playbook ${env.ANSIBLE_DEPLOY_YML}"
                }
            }
        }
    }
    post {
        success {
            // email

        }
        failure {
            // email
            
        }
    }
}
