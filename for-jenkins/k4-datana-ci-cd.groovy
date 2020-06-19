#!groovy
// Check ub1 properties
properties([disableConcurrentBuilds()])

pipeline {
    agent {
        label 'master'
    }
    options {
        buildDiscarder(logRotator(numToKeepStr: '10', artifactNumToKeepStr: '10'))
        timestamps()
    }
    stages {
        stage("k2 - Checkout") {
            steps {
                checkout([$class: 'GitSCM', branches: [[name: 'Generator_REST_BY_SIEMENS']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'kakunin', url: 'https://gitlab.dds.lanit.ru']]])
            }
        }
        stage("k2 - Build") {
            steps {
                withMaven(maven: 'maven3') {
                    sh "mvn clean compile package spring-boot:repackage -P plcServer "
                }
            }
        }

        stage("Telegram step") {
            steps {
                gitVar = git(branch: 'Generator_REST_BY_SIEMENS', credentialsId: 'KostyaDatanaV5', url: "git@gitlab.dds.lanit.ru:datana_smart/tools-adapters.git")
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
                sh "curl - x socks5://proxyuser:secure@94.177.216.245:777 -X POST \"https://api.telegram.org/bot1180854473:AAG1BHnbcM4oRRZW2-DKbZMYD2WqkDtUesU/sendMessage?chat_id=-1001325011128&parse_mode=HTML&text=Собрал. "
                +"GIT_COMMITTER_NAME = " + gitVar.GIT_COMMITTER_NAME
                +"GIT_AUTHOR_NAME = " + gitVar.GIT_AUTHOR_NAME
            }
        }
    }
}
