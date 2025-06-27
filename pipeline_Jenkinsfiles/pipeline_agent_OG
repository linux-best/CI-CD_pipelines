pipeline {
    agent any

    environment {
        YARA_RULE = '/var/lib/jenkins/yara_rules/pipeline_rule.yara'
        STRING_VAR = "" // Condition_Mode_String
        SONAR_HOME =  "/opt/sonar-scanner/bin/sonar-scanner"
        ANSIBLE_DEPLOY_YML = 'play.yml'
        CODE_CHANGE_1 = false
        CODE_CHANGE_2 = false
        REPO_SCRIPTS = 'https://github.com/linux-best/scripts/'
        REPO_ANSIBLE = 'https://github.com/linux-best/ansible/'
        REPO_PROJECTS_PATH = '/var/lib/jenkins/repo_projects/'
        REPO_1 = 'ansible'
        REPO_2 = 'scripts'
    }

    stages {
        stage("Checkout_SCM ==> ansible") {
            steps {
                echo "stage ==> [SCM Versioning ...... ansible]"
                dir("${env.REPO_1}") {
                    git branch: "main" , url: "${env.REPO_ANSIBLE}"
                }
            }
        }
        stage("Checkout_SCM ==> scripts") {
            steps {
                echo "stage ==> [SCM Versioning ...... scripts]"
                dir("${env.REPO_2}") {
                    git branch: "main" , url: "${env.REPO_SCRIPTS}"
                }
            }
        }
        stage ("Build") {
            steps {
                echo "Building stage ....."
                dir("${env.REPO_2}") {
                   // sh "bash update.sh"
                    sh "pip install -r packages/requierment.txt"
                    sh "bash requierment_linux.sh"
                }
            }
        }
        stage("Git_Code_Check") {
            parallel {
                stage('Task A ==> Deploy_Repo') {
                    steps {
                        script {
                            def var_Git_Remote = sh(script: """
                            cd ${env.REPO_PROJECTS_PATH}${env.REPO_1} &&
                            git diff && git fetch &&
                            git diff HEAD origin ||
                            echo "[msg] please pull the changes from remote with : "git pull" " || echo "[msg] no changes on remote"
                            ls -l
                            pwd
                            """
                            ,returnStdout: true)
                            def var_Git_Local = sh(script: "cd ${env.REPO_PROJECTS_PATH}${env.REPO_1} && git diff --name-only",returnStdout: true).trim()
                            if (var_Git_Local == "${env.STRING_VAR}") {
                                echo "[msg] no changes detected ==> [ NONE ]" // python_log_argumant
                                echo "${var_Git_Remote}"
                            } else {
                                echo "[msg] changes detected ==> [ ${var_Git_Local} ]" // python_log_argumant
                                echo "${var_Git_Remote}"
                                env.CODE_CHANGE_1 = true
                            }
                        }
                    }
                }
                stage('Task A ==> Build/Test_Repo') {
                    steps {
                        script {
                            def var_Git_Remote = sh(script: """
                            cd ${env.REPO_PROJECTS_PATH}${env.REPO_2} &&
                            git diff && git fetch &&
                            git diff HEAD origin/main ||
                            echo "[msg] please pull the changes from remote with : "git pull" " || echo "[msg] no changes on remote"
                            ls -l
                            pwd
                            """
                            ,returnStdout: true)
                            def var_Git_Local = sh(script: "cd ${env.REPO_PROJECTS_PATH}${env.REPO_2} && git diff --name-only",returnStdout: true).trim()
                            if (var_Git_Local == "${env.STRING_VAR}") {
                                echo "[msg] no changes detected ==> [ NONE ]" // python_log_argumant
                                echo "${var_Git_Remote}"
                            } else {
                                echo "[msg] changes detected ==> [ ${var_Git_Local} ]" // python_log_argumant
                                echo "${var_Git_Remote}"
                                env.CODE_CHANGE_2 = true
                            }
                        }
                    }
                }
            }
        }
        stage("Yara_Security_Code") {
            when {
                expression {
                    env.CODE_CHANGE_1 != false || env.CODE_CHANGE_2 != false
                }
            }
            steps {
                script {
                    def yara_Scan_1 = sh(script: "yara ${env.YARA_RULE} ${REPO_1} | grep -q .",returnStatus:true)
                    def yara_Scan_1_output = sh(script: "yara ${env.YARA_RULE} ${REPO_1}",returnStdout:true).trim()
                    def yara_Scan_2 = sh(script: "yara ${env.YARA_RULE} ${REPO_2} | grep -q .",returnStatus:true)
                    def yara_Scan_2_output = sh(script: "yara ${env.YARA_RULE} ${REPO_2}",returnStdout:true).trim()
                    if (yara_Scan_1 == 1 && yara_Scan_2 == 1 ) {
                        echo "No Malicious File Found !"
                    } else {
                        echo "Malicious Files ==> ${REPO_1}:${yara_Scan_1_output} | ${REPO_2}:${yara_Scan_2_output}"
                        error("Malicious File Found !! status:=> ${REPO_1}:${yara_Scan_1} | ${REPO_2}:${yara_Scan_2} ")
                    }
                }
            }
        }
        stage("Code Analysis") {
            parallel {
                stage ("Task A ==> Analysis") {
                    steps {
                        dir("${env.REPO_1}") {
                            withSonarQubeEnv('localSonarqube') {
                                sh"${env.SONAR_HOME}"
                            }
                        }
                    }
                }
                stage ("Task B ==> Analysis") {
                    steps {
                        dir("${env.REPO_2}") {
                            withSonarQubeEnv('localSonarqube') {
                                sh"${env.SONAR_HOME}"
                            }
                        }
                    }
                }
                stage ("Task B ==> Message ") {
                    steps {
                        echo "Task B Analysis Finished !" // python_log_argument
                    }
                }
            }
        }
        stage("Quality Gate") {
            parallel {
                stage ("Task A ==> Quality Gate") {
                    steps {
                        dir("${env.REPO_1}") {
                            script {
                                def value_Quality_Gate = waitForQualityGate()
                                if (value_Quality_Gate.status == "OK") {
                                    echo "Quality Check :Success:> ${value_Quality_Gate.status}"
                                } else {
                                    error("Quality Check :Failure:> ${value_Quality_Gate.status}")
                                }
                            }
                        }
                    }
                }
                stage ("Task B ==> Quality Gate") {
                    steps {
                        dir("${env.REPO_2}") {
                            script {
                                def value = waitForQualityGate()
                                if (value.status == "OK") {
                                    echo "Quality Check :Success:> ${value.status}"
                                } else {
                                    error("Quality Check :Failure:> ${value.status}")
                                }
                            }
                        }
                    }
                }
                stage ("Task B ==> Message") {
                    steps {
                        echo "Task B Quality Gate Finished !" // python_log_argument
                    }
                }
            }
        }
        stage("Checkout_SCM_Agent ==> scripts") {
            agent {
                label 'slave'
            }
            steps {
                echo "stage ==> [SCM Versioning ...... scripts]"
                dir("${env.REPO_2}") {
                    git branch: "main" , url: "${env.REPO_SCRIPTS}"
                }
            }
        }
        stage ("Build_Agent") {
            agent {
                label 'slave'
            }
            steps {
                echo "Building stage ....."
                dir("${env.REPO_2}") {
                    sh "bash update.sh"
                    sh "bash requierment_linux.sh"
                }
            }
        }
        stage("Test_Agent") {
            agent {
                label 'slave'
            }
            steps {
                dir("${env.REPO_2}") {
                    script {
                        def value = sh(script:'python3 Health_check.py',returnStdout: true).trim()
                        if (value == "ok") {
                            echo "connection to INTERNET ===> SAFE+ via Agent"
                            echo "connection to DEPLOYMENT_SERVER ===> SAFE+ via Agent"
                            echo "connection is OK !"
                        } else {
                            echo "connection to INTERNET ===> LOST- via Agent"
                            echo "connection to DEPLOYMENT_SERVER ===> LOST- via Agent"
                            error("Connection Lost !!")
                        }
                    }
                }
            }
        }
        stage ("Test") {
            parallel {
                stage ('Task A') {
                    environment {
                        BACKUP_SH = "backup.sh"
                        BACKUP_SH_DESTINATION = "/var/lib/jenkins/backup/"
                    }
                    steps {
                        dir("${env.REPO_2}") {
                            sh "bash ${env.BACKUP_SH} ${env.REPO_PROJECTS_PATH}${env.REPO_1} ${env.BACKUP_SH_DESTINATION} ${env.REPO_1}_backup_${BUILD_NUMBER}"
                            sh "bash ${env.BACKUP_SH} ${env.REPO_PROJECTS_PATH}${env.REPO_2} ${env.BACKUP_SH_DESTINATION} ${env.REPO_2}_backup_${BUILD_NUMBER}"
                        }
                    }
                }
                stage ('Task B') {
                    steps {
                        dir("${env.REPO_2}") {
                            script {
                                def value = sh(script:'python3 Health_check.py',returnStdout: true).trim()
                                if (value == "ok") {
                                    echo "connection to INTERNET ===> SAFE+"
                                    echo "connection to DEPLOYMENT_SERVER ===> SAFE+"
                                    echo "connection is OK !"
                                } else {
                                    echo "connection to INTERNET ===> LOST-"
                                    echo "connection to DEPLOYMENT_SERVER ===> LOST-"
                                    error("Connection Lost !!")
                                }
                            }
                        }
                    }
                }
            }
        }
        stage ('Deploy') {
            steps {
                echo "Deploying application .... "
                dir("${env.REPO_1}") {
                    sh "sudo -u javande ansible-playbook playbooks/${env.ANSIBLE_DEPLOY_YML}"
                }
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
