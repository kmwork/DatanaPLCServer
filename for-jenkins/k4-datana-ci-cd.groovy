#!groovy
// Check ub1 properties
properties([disableConcurrentBuilds()])

pipeline {
    agent {
        label 'master'
    }
    environment {
        constGitBranch = 'Generator_REST_BY_SIEMENS'
        constGitUrl = 'git@gitlab.dds.lanit.ru:datana_smart/tools-adapters.git'
        constGitCredentialsId = 'kostya5'
    }

    tools {
        maven 'Maven 3.6'
        jdk 'jdk13'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10', artifactNumToKeepStr: '10'))
        timestamps()
    }
    stages {
        stage('k2 - Checkout') {
            steps {
                echo "User:" + constGitCredentialsId + "\n" + "GitBranch: " + constGitBranch
                checkout([$class: 'GitSCM', branches: [[name: env.constGitBranch]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: env.constGitCredentialsId, url: env.constGitUrl]]])
            }
        }

        script {
            sh "mvn clean compile package spring-boot:repackage -P plcServer "
        }

        stage('Telegram step') {
            steps {
                script {
                    gitVar = git(branch: env.constGitBranch, credentialsId: env.constGitCredentialsId, url: env.constGitUrl)
                    /* echo gitVar.GIT_COMMIT
                        Fields:

                        GIT_AUTHOR_EMAIL
                        GIT_AUTHOR_NAME
                        GIT_BRANCH
                        GIT_COMMIT
                        GIT_COMMITTER_EMAIL
                        GIT_COMMITTER_NAME
                        GIT_LOCAL_BRANCH
                        GIT_PREVIOUS_COMMIT
                        GIT_PREVIOUS_SUCCESSFUL_COMMIT
                        GIT_URL */
                    sh 'curl - x socks5://proxyuser:secure@94.177.216.245:777 -X POST "https://api.telegram.org/bot1180854473:AAG1BHnbcM4oRRZW2-DKbZMYD2WqkDtUesU/sendMessage?chat_id=-1001325011128&parse_mode=HTML&text=Собрал. '
                    +'GIT_COMMITTER_NAME = ' + gitVar.GIT_COMMITTER_NAME
                    +'GIT_AUTHOR_NAME = ' + gitVar.GIT_AUTHOR_NAME
                    +'"'
                }
            }
        }
    }
}
