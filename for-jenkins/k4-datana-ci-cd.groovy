/** пример Jenkins сборки PipeLine **/
/** сделано по JIRA задаче : https://jira.dds.lanit.ru/browse/NKR-465 **/

env.constGitBranch = 'Generator_REST_BY_SIEMENS'
env.constGitUrl = 'git@gitlab.dds.lanit.ru:datana_smart/tools-adapters.git'
env.constGitCredentialsId = 'kostya5'
env.constMVN_HOME = '/home/lin/apps/apache-maven-3.5.4'
env.constJAVA_HOME = '/home/lin/apps/jdk13'
env.constDockerDomain = "registry.hub.docker.com"
env.constDockerRegistry = "https://$env.constDockerDomain"

env.constDockerName = "kmtemp"
env.constDockerTag = "datana"
env.constDockerImageVersion = "2"

env.constDockerRegistryLogin = "kmtemp";

env.constTelegramURL = "https://api.telegram.org/bot1180854473:AAG1BHnbcM4oRRZW2-DKbZMYD2WqkDtUesU/sendMessage?chat_id=-1001325011128&parse_mode=HTML"
env.allJob = JOB_NAME;
env.Version = "0.0.${BUILD_NUMBER}"

def lastSuccessfulBuild(passedBuilds, build) {
    if ((build != null) && (build.result != 'SUCCESS')) {
        passedBuilds.add(build)
        lastSuccessfulBuild(passedBuilds, build.getPreviousBuild())
    }
}

def sendTelegram(String msg) {
    sh "/usr/bin/curl -X POST  \"${env.constTelegramURL}\" -d \"text=${msg}\""
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
                def commentcut2 = commentcut
                echo "${commentcut}"
                def urls = ""
                commentcut.eachMatch("NKR-[0-9]+") {
                    ch ->
                        urls += '<a href=\\"' + "\"https://jira.dds.lanit.ru/browse/${ch}\"" + '\\">' + "${ch}</a> "
                        commentcut2 = commentcut2.replaceAll("${ch}", "")
                }
                echo "Comment: ${comment}"
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
            env.PATH = "$env.constMVN_HOME/bin:$env.constJAVA_HOME/bin:$PATH"
            passedBuilds = []
            lastSuccessfulBuild(passedBuilds, currentBuild);

            def changeLog = getChangeLog(passedBuilds, Version)
            if (changeLog.trim() == '') {
                changeLog = 'нет изменений'
            }

            sendTelegram("Начинаю сборку:  ${allJob}. build ${BUILD_NUMBER}\nВ этой серии вы увидите: \n ${changeLog}");

            echo "[PARAM] PATH=$PATH"
            echo "-----------------------------------"
            echo sh(script: 'env|sort', returnStdout: true)
            echo "==================================="
        }
        stage('step-1: Checkout') {
            echo 'Building'
            checkout([$class: 'GitSCM', branches: [[name: env.constGitBranch]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: env.constGitCredentialsId, url: env.constGitUrl]]])
        }

        stage('step-2: Build by maven') {
            sh "mvn clean compile package spring-boot:repackage -P plcServer"
        }
        stage('step-3: Docker remove') {
            sh "docker stop $env.constDockerName || true && docker rm $env.constDockerName || true"
            sh "docker stop $env.constDockerDomain/$env.constDockerName/$env.constDockerTag || true && docker rm $env.constDockerDomain/$env.constDockerName/$env.constDockerTag || true"
        }


        stage('step-4: Docker build') {
            sh "docker build --tag=$env.constDockerName/$env.constDockerDomain/$env.constDockerTag:$env.constDockerImageVersion ."
        }

        stage('step-5: Docker create') {
            sh "docker create $env.constDockerName/$env.constDockerTag"
            sh "docker run -d -v /var/run/docker.sock:/var/run/docker.sock -v \$(which docker):/usr/bin/docker -p 9999:8080 $env.constDockerName/$env.constDockerTag"
            sh "docker tag $env.constDockerName/$env.constDockerTag $env.constDockerDomain/$env.constDockerName/$env.constDockerTag:$env.constDockerImageVersion"
        }

        stage('step-6: Docker pull') {
            sh "cat /home/lin/apps/datana-docker-secret/rep-password.txt | docker login --password-stdin --username=${env.constDockerRegistryLogin} ${env.constDockerRegistry}"
            sh "docker push $env.constDockerDomain/$env.constDockerName/$env.constDockerTag:$env.constDockerImageVersion"
        }


        stage('step-7: Telegram step') {
            sendTelegram("Сборка завершена ${env.allJob}. build ${env.BUILD_NUMBER}")
        }
    }


} catch (e) {
    currentBuild.result = "FAILED"
    node {
        sendTelegram("Сборка сломалась ${env.allJob}. build ${env.BUILD_NUMBER}")
    }
    throw e
}

