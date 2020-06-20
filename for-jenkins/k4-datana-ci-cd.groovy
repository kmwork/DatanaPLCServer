node {
//    options {
//        timestamps()
//        ansiColor('xterm')
//
//    }

    stage('step-0: Init') {
        def constGitBranch = 'Generator_REST_BY_SIEMENS'
        def constGitUrl = 'git@gitlab.dds.lanit.ru:datana_smart/tools-adapters.git'
        def constGitCredentialsId = 'kostya5'
        def gitVar = git(branch: constGitBranch, credentialsId: constGitCredentialsId, url: constGitUrl)
        def constGIT_COMMITTER_NAME = gitVar.GIT_COMMITTER_NAME
        def constGIT_AUTHOR_NAME = gitVar.GIT_AUTHOR_NAME
        echo "User:" + constGitCredentialsId + "\n" + "GitBranch: " + constGitBranch
    }
    stage('step-1: Checkout') {
        checkout([$class: 'GitSCM', branches: [[name: constGitBranch]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: constGitCredentialsId, url: constGitUrl]]])
    }

    stage('step-2: Build by maven') {

        withMaven(jdk: 'jdk13', maven: 'maven3') {
            sh 'mvn clean compile package spring-boot:repackage -P plcServer'
        }
    }

    stage('step-3: Telegram step') {
        sh "curl - x socks5://proxyuser:secure@94.177.216.245:777 -X POST \"https://api.telegram.org/bot1180854473:AAG1BHnbcM4oRRZW2-DKbZMYD2WqkDtUesU/sendMessage?chat_id=-1001325011128&parse_mode=HTML&text=Builed.+GIT_COMMITTER_NAME+=+$env.constGIT_COMMITTER_NAME,+GIT_AUTHOR_NAME+=+$env.constGIT_AUTHOR_NAME\""
    }
}

