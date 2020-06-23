def constGitBranch
def constGitUrl
def constGitCredentialsId
def constMVN_HOME
def gitVar
def constTelegramURL
def constProxyTelegram
def constDockerRegistry
def constDockerName
def constDockerRegistryLogin
def constDockerRegistryPassword
def constDockerDomain
def constDockerTag
def constDockerImageVersion
def allJob = env.JOB_NAME.tokenize('/') as String[];
def Version = "0.0.${BUILD_NUMBER}"

def lastSuccessfulBuild(passedBuilds, build) {
    if ((build != null) && (build.result != 'SUCCESS')) {
        passedBuilds.add(build)
        lastSuccessfulBuild(passedBuilds, build.getPreviousBuild())
    }
}

def sendTelegram (String msg){
    sh "/usr/bin/curl -X POST  \"$constTelegramURL\" -d \"text=Начинаю сборку:  ${msg}\""
}

@NonCPS
def getChangeLog(passedBuilds, Version) {
    def log = ""
    for (int x = 0; x < passedBuilds.size(); x++) {
        def currentBuild = passedBuilds[x];
        def changeLogSets = currentBuild.rawBuild.changeSets
        for (int i = 0; i < changeLogSets.size(); i++) {
            def entries = changeLogSets[i].items
            for (int j = 0; j < entries.length; j++) {
                def entry = entries[j]
                def comment = entry.msg
                def jurl ="https://jira.dds.lanit.ru/browse/"
                def commentcut = comment.replaceAll("${jurl}", "")
                echo "${commentcut}"
                def urls = ""
                def commentcut2 = commentcut
                commentcut.eachMatch("MDMW19-[0-9]+") {
                    ch -> urls += '<a href=\\"' + "\"https://jira.dds.lanit.ru/browse/${ch}\"" + '\\">' + "${ch}</a> "
                        commentcut2 = commentcut2.replaceAll("${ch}", "")
                        try {
                            //jiraAddComment comment: "${Version}", idOrKey: "${ch}", site: 'Jira'
                        }
                        catch (e) {
                            echo "ERR"

                        }
                }
                echo "Comment: ${commentcut2}"
                echo "Tasks: ${urls}"


                log += "${j+1}. by ${entry.author} on ${new Date(entry.timestamp)}\nComment: ${commentcut2} \nTask: ${urls}\n"


            }
            log +="\n"
        }
    }
    return log;
}

try {
    node {
        stage('step-0: Init') {
            constGitBranch = 'Generator_REST_BY_SIEMENS'
            constGitUrl = 'git@gitlab.dds.lanit.ru:datana_smart/tools-adapters.git'
            constGitCredentialsId = 'kostya5'
            gitVar = git(branch: constGitBranch, credentialsId: constGitCredentialsId, url: constGitUrl)
            constMVN_HOME = '/home/lin/apps/apache-maven-3.5.4'
            echo "User:" + constGitCredentialsId + "\n" + "GitBranch: " + constGitBranch
            constJAVA_HOME = '/home/lin/apps/jdk13'
            env.PATH = "$constMVN_HOME/bin:$constJAVA_HOME/bin:$PATH"
            //constDockerRegistry = "https://hub.docker.com/repository/docker/kmtemp/datana"
            constDockerDomain = "registry.hub.docker.com"
            constDockerRegistry = "https://$constDockerDomain"

            constTelegramURL = "https://api.telegram.org/bot1180854473:AAG1BHnbcM4oRRZW2-DKbZMYD2WqkDtUesU/sendMessage?chat_id=-1001325011128&parse_mode=HTML"
            constProxyTelegram = "socks5://proxyuser:secure@94.177.216.245:777"
            constDockerName = "kmtemp"
            constDockerTag = "datana"
            constDockerImageVersion = "2"

            constDockerRegistryLogin = "kmtemp";
            constDockerRegistryPassword = "kostya-docker-2";


            passedBuilds = []
            lastSuccessfulBuild(passedBuilds, currentBuild);

            def changeLog = getChangeLog(passedBuilds, Version)
            if (changeLog.trim() == '') {
                changeLog = 'нет изменений'
            }

            sendTelegram("Начинаю сборку:  ${allJob[0]}/${allJob[1]}/${allJob[2]}. build ${BUILD_NUMBER}\nВ этой серии вы увидите: \n ${changeLog}");

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
            sh "docker build --tag=$constDockerName/$constDockerDomain/$constDockerTag:$constDockerImageVersion ."
        }

        stage('step-4: Docker remove') {
            sh "docker stop $constDockerName || true && docker rm $constDockerName || true"
            sh "docker stop $constDockerDomain/$constDockerName/$constDockerTag || true && docker rm $constDockerDomain/$constDockerName/$constDockerTag || true"
        }


        stage('step-5: Docker create') {
            sh "docker create $constDockerName/$constDockerTag"
            sh "docker tag $constDockerName/$constDockerTag $constDockerDomain/$constDockerName/$constDockerTag:$constDockerImageVersion"
        }

        stage('step-6: Docker pull') {
            sh "cat /home/lin/apps/datana-docker-secret/rep-password.txt | docker login --password-stdin --username=$constDockerName $constDockerRegistry"
            sh "docker push $constDockerDomain/$constDockerName/$constDockerTag:$constDockerImageVersion"
        }


        stage('step-7: Telegram step') {
            def DatanaAuthor = sh script: "git show -s --pretty=\"%an <%ae>\" ${gitVar.GIT_COMMIT}", returnStdout: true
            DatanaAuthor = DatanaAuthor.replace("@", " ")
            DatanaAuthor = DatanaAuthor.replace("<", " ")
            DatanaAuthor = DatanaAuthor.replace(">", " ")
            echo DatanaAuthor

            sendTelegram("Build with Success, DatanaAuthor = $DatanaAuthor. Number of build = {env.BUILD_NUMBER}")
        }
    }


} catch (e) {
    currentBuild.result = "FAILED"
    node {
        sendTelegram("Сборка сломалась ${allJob[0]}/${allJob[1]}/${allJob[2]}. build ${BUILD_NUMBER}")
    }
    throw e
}

