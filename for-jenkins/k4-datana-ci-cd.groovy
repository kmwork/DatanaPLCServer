env.env.constGitBranch = 'Generator_REST_BY_SIEMENS'
env.env.constGitUrl = 'git@gitlab.dds.lanit.ru:datana_smart/tools-adapters.git'
env.env.constGitCredentialsId = 'kostya5'
gitVar = git(branch: env.env.constGitBranch, credentialsId: env.env.constGitCredentialsId, url: env.env.constGitUrl)
env.env.constMVN_HOME = '/home/lin/apps/apache-maven-3.5.4'
echo "User:" + env.env.constGitCredentialsId + "\n" + "GitBranch: " + env.env.constGitBranch
env.env.constJAVA_HOME = '/home/lin/apps/jdk13'
env.env.constDockerDomain = "registry.hub.docker.com"
env.env.constDockerRegistry = "https://$env.env.constDockerDomain"

env.env.constDockerName = "kmtemp"
env.env.constDockerTag = "datana"
env.env.constDockerImageVersion = "2"

env.env.constDockerRegistryLogin = "kmtemp";

env.env.constTelegramURL = "https://api.telegram.org/bot1180854473:AAG1BHnbcM4oRRZW2-DKbZMYD2WqkDtUesU/sendMessage?chat_id=-1001325011128&parse_mode=HTML"
allJob = JOB_NAME;
Version = "0.0.${BUILD_NUMBER}"

def lastSuccessfulBuild(passedBuilds, build) {
    if ((build != null) && (build.result != 'SUCCESS')) {
        passedBuilds.add(build)
        lastSuccessfulBuild(passedBuilds, build.getPreviousBuild())
    }
}

def sendTelegram(String msg) {
    sh "/usr/bin/curl -X POST  \"${env.env.constTelegramURL}\" -d \"text=${msg}\""
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
                def jurl = "https://jira.dds.lanit.ru/browse/"
                def commentcut = comment.replaceAll("${jurl}", "")
                echo "${commentcut}"
                def urls = ""
                def commentcut2 = commentcut
                commentcut.eachMatch("MDMW19-[0-9]+") {
                    ch ->
                        urls += '<a href=\\"' + "\"https://jira.dds.lanit.ru/browse/${ch}\"" + '\\">' + "${ch}</a> "
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


                log += "${j + 1}. by ${entry.author} on ${new Date(entry.timestamp)}\nComment: ${commentcut2} \nTask: ${urls}\n"


            }
            log += "\n"
        }
    }
    return log;
}

try {
    node {
        stage('step-0: Init') {
            env.PATH = "$env.env.constMVN_HOME/bin:$env.env.constJAVA_HOME/bin:$PATH"
            passedBuilds = []
            lastSuccessfulBuild(passedBuilds, currentBuild);

            def changeLog = getChangeLog(passedBuilds, Version)
            if (changeLog.trim() == '') {
                changeLog = 'нет изменений'
            }

            sendTelegram("Начинаю сборку:  ${allJob}. build ${BUILD_NUMBER}\nВ этой серии вы увидите: \n ${changeLog}");

            echo "[PARAM] PATH=$PATH"
            echo "[PARAM] gitVar=$gitVar"
            echo "-----------------------------------"
            echo sh(script: 'env|sort', returnStdout: true)
            echo "==================================="
        }
        stage('step-1: Checkout') {
            echo 'Building'
            checkout([$class: 'GitSCM', branches: [[name: env.env.constGitBranch]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: env.env.constGitCredentialsId, url: env.env.constGitUrl]]])
        }

        stage('step-2: Build by maven') {
            sh "mvn clean compile package spring-boot:repackage -P plcServer"
        }

        stage('step-3: Docker build') {
            sh "docker build --tag=$env.env.constDockerName/$env.env.constDockerDomain/$env.constDockerTag:$env.constDockerImageVersion ."
        }

        stage('step-4: Docker remove') {
            sh "docker stop $env.constDockerName || true && docker rm $env.constDockerName || true"
            sh "docker stop $env.constDockerDomain/$env.constDockerName/$env.constDockerTag || true && docker rm $env.constDockerDomain/$env.constDockerName/$env.constDockerTag || true"
        }


        stage('step-5: Docker create') {
            sh "docker create $env.constDockerName/$env.constDockerTag"
            sh "docker tag $env.constDockerName/$env.constDockerTag $env.constDockerDomain/$env.constDockerName/$env.constDockerTag:$env.constDockerImageVersion"
        }

        stage('step-6: Docker pull') {
            sh "cat /home/lin/apps/datana-docker-secret/rep-password.txt | docker login --password-stdin --username=${env.constDockerRegistryLogin} ${env.constDockerRegistry}"
            sh "docker push $env.constDockerDomain/$env.constDockerName/$env.constDockerTag:$env.constDockerImageVersion"
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
        sendTelegram("Сборка сломалась ${allJob}. build ${BUILD_NUMBER}")
    }
    throw e
}

