node {
//    options {
//        timestamps()
//        ansiColor('xterm')
//
//    }


    def constGitBranch
    def constGitUrl
    def constGitCredentialsId
    def constMVN_HOME
    def gitVar
    def constTelegramURL
    def constProxyTelegram
    stage('step-0: Init') {
        constGitBranch = 'Generator_REST_BY_SIEMENS'
        constGitUrl = 'git@gitlab.dds.lanit.ru:datana_smart/tools-adapters.git'
        constGitCredentialsId = 'kostya5'
        gitVar = git(branch: constGitBranch, credentialsId: constGitCredentialsId, url: constGitUrl)
        constMVN_HOME = '/home/lin/apps/apache-maven-3.5.4'
        echo "User:" + constGitCredentialsId + "\n" + "GitBranch: " + constGitBranch
        constJAVA_HOME = '/home/lin/apps/jdk13'
        env.PATH = "$constMVN_HOME/bin:$constJAVA_HOME/bin:$PATH"

        constTelegramURL = "https://api.telegram.org/bot1180854473:AAG1BHnbcM4oRRZW2-DKbZMYD2WqkDtUesU/sendMessage?chat_id=-1001325011128&parse_mode=HTML"
        constProxyTelegram = "socks5://proxyuser:secure@94.177.216.245:777"
        echo "[PARAM] PATH=$PATH"
        echo "[PARAM] gitVar=$gitVar"
        echo "-----------------------------------"
        echo sh(script: 'env|sort', returnStdout: true)
        echo "==================================="
    }
    stage('step-1: Checkout') {
        echo 'Building'
        checkout([$class: 'GitSCM', branches: [[name: constGitBranch]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: constGitCredentialsId, url: constGitUrl]]])
    }

    stage('step-2: Build by maven') {
        sh "mvn clean compile package spring-boot:repackage -P plcServer"
    }

    stage('step-3: Docker build') {
        sh "docker build -t=kmtemp/datana ."
    }

    stage('step-4: Docker remove') {
        sh "docker container rm kmtemp"
    }


    stage('step-5: Telegram step') {
        def DatanaAuthor = sh script: "git show -s --pretty=\"%an <%ae>\" ${gitVar.GIT_COMMIT}", returnStdout: true
        DatanaAuthor = DatanaAuthor.replace("@", " ")
        DatanaAuthor = DatanaAuthor.replace("<", " ")
        DatanaAuthor = DatanaAuthor.replace(">", " ")
        echo DatanaAuthor

        def valueMessageAsText = ",DatanaAuthor=$DatanaAuthor"
        echo valueMessageAsText
        //sh "curl -x $constProxyTelegram  -d text='\"$valueMessageAsText\"' -X POST $constTelegramURL"
        sh "curl -d text=\"$valueMessageAsText\" -X POST \"$constTelegramURL\""

    }
}

