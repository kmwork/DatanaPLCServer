#!groovy
// Check datana-first-k1 properties
node('datana-first-k1') {
    options {
        timestamps()
        ansiColor('xterm')

    }
    stage('step-0: Init') {
        def constGitBranch = 'Generator_REST_BY_SIEMENS'
        def constGitUrl = 'git@gitlab.dds.lanit.ru:datana_smart/tools-adapters.git'
        def constGitCredentialsId = 'kostya5'
        def gitVar = git(branch: env.constGitBranch, credentialsId: env.constGitCredentialsId, url: env.constGitUrl)
        def constGIT_COMMITTER_NAME = "${env.gitVar.GIT_COMMITTER_NAME}"
        def constGIT_AUTHOR_NAME = "${env.gitVar.GIT_AUTHOR_NAME}"
    }
    stage('step-1: Checkout') {
        echo "User:" + constGitCredentialsId + "\n" + "GitBranch: " + constGitBranch
        checkout([$class: 'GitSCM', branches: [[name: env.constGitBranch]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: env.constGitCredentialsId, url: env.constGitUrl]]])
    }

    stage('step-2: Build by maven') {
        sh "mvn clean compile package spring-boot:repackage -P plcServer "
    }

    stage('step-3: Telegram step') {
        sh "curl - x socks5://proxyuser:secure@94.177.216.245:777 -X POST \"https://api.telegram.org/bot1180854473:AAG1BHnbcM4oRRZW2-DKbZMYD2WqkDtUesU/sendMessage?chat_id=-1001325011128&parse_mode=HTML&text=Builed.+GIT_COMMITTER_NAME+=+$env.constGIT_COMMITTER_NAME,+GIT_AUTHOR_NAME+=+$env.constGIT_AUTHOR_NAME\""
    }
}
