def lastSuccessfulBuild(passedBuilds, build) {
    if ((build != null) && (build.result != 'SUCCESS')) {
        passedBuilds.add(build)
        lastSuccessfulBuild(passedBuilds, build.getPreviousBuild())
    }
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
def allJob = env.JOB_NAME.tokenize('/') as String[];
def Version = "1.2.0.${BUILD_NUMBER}"
try {
    node ("asha-all") {
        stage("Check git"){
            cleanWs()


            checkout([$class: 'GitSCM', branches: [[name: '*/develop']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'mdm']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'asha', url: 'git@gitlab.dds.lanit.ru:mdm/mdm-frontend.git']]])
            checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'mdm-f2']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'asha', url: 'git@gitlab.dds.lanit.ru:mdm/mdm-front-rea.git']]])

            passedBuilds = []
            lastSuccessfulBuild(passedBuilds, currentBuild);

            def changeLog = getChangeLog(passedBuilds, Version)
            if  (changeLog.trim() == '') { changeLog = 'нет изменений' }
            sh "/usr/bin/curl -x socks5://proxyuser:secure@94.177.216.245:777 -X POST \"https://api.telegram.org/bot1018975900:AAFTH5MxjrzpSi-v0ivkw2TYmNIQLYf_ak0/sendMessage\" -d \"chat_id=-1001393370950&parse_mode=HTML&text=Начинаю сборку:  ${allJob[0]}/${allJob[1]}/${allJob[2]} 1.2.0 build ${BUILD_NUMBER}\nВ этой серии вы увидите: \n ${changeLog}\""
            checkout([$class: 'GitSCM', branches: [[name: '*/develop']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'mdm-devops']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'asha', url: 'git@gitlab.dds.lanit.ru:mdm/mdm-devops.git']]])

        }

        stage("Build DOCKER") {

            sh 'mkdir front'
            sh 'mv mdm-f2 front/frontend2'
            sh 'mv mdm/frontend front/frontend'
            sh 'cp -f mdm-devops/env/common/docker/front front/dockerfile'
            sh 'cp -f mdm-devops/env/common/nginx/nginx.conf front/nginx.conf'
            sh 'cp -f mdm-devops/env/common/hack/menu.js ./front/frontend2/src/modules/feature-ui-app-semantic-project/common/menu.js'
            sh 'sed -i "s|mdm2019.dds.lanit.ru|xn--d1apb.xn--d1achjhdicc8bh4h.xn--p1ai|g" ./front/frontend/.env.production'
            sh 'sed -i "s|mdm2019.dds.lanit.ru|xn--d1apb.xn--d1achjhdicc8bh4h.xn--p1ai|g" ./front/frontend2/config/production.js'
            sh 'sed -i "s|mdm2019.dds.lanit.ru|xn--d1apb.xn--d1achjhdicc8bh4h.xn--p1ai|g" ./front/frontend2/src/modules/feature-ui-app-semantic-project/common/menu.js'
            sh 'sed -i "s|mdm2019.dds.lanit.ru|xn--d1apb.xn--d1achjhdicc8bh4h.xn--p1ai|g" ./front/frontend/src/views/public/RedirectReahome/index.vue'

            dir("front") {
                withCredentials([usernamePassword( credentialsId: 'registry_cred', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                    sh 'docker login -u ${USERNAME} -p ${PASSWORD} registry.gitlab.dds.lanit.ru'
                    sh "docker build -t registry.gitlab.dds.lanit.ru/mdm/mdm:mdm2019-front-${Version} ."
                    sh "docker push registry.gitlab.dds.lanit.ru/mdm/mdm:mdm2019-front-${Version}"
                }
                sh 'docker login -u mdmuser -p mdmpassword mdmreg.dds.lanit.ru'
                sh "docker image tag registry.gitlab.dds.lanit.ru/mdm/mdm:mdm2019-front-${Version} mdmreg.dds.lanit.ru/mdm2019:mdm2019-front-${Version}"
                sh "docker push mdmreg.dds.lanit.ru/mdm2019:mdm2019-front-${Version}"
            }

        }
    }


    node ("mdm2019-dev") {
        stage("Deploy all"){

            dir("/srv/mdm") {
                withCredentials([usernamePassword( credentialsId: 'registry_cred', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                    sh 'docker login -u ${USERNAME} -p ${PASSWORD} registry.gitlab.dds.lanit.ru'
                    sh "sed -i -r 's|mdm2019-front-1.2.0.[0-9]+|mdm2019-front-1.2.0.${BUILD_NUMBER}|g' ./docker-compose.yml"
                    sh 'docker-compose pull front'
                    sh 'docker-compose stop front'
                    sh 'docker-compose rm -f front'
                    sh 'docker-compose up -d front'
                    sh "/usr/bin/curl -x socks5://proxyuser:secure@94.177.216.245:777 -X POST \"https://api.telegram.org/bot1018975900:AAFTH5MxjrzpSi-v0ivkw2TYmNIQLYf_ak0/sendMessage\" -d \"chat_id=-1001393370950&parse_mode=HTML&text=Накатил сборку, ${allJob[0]}/${allJob[1]}/${allJob[2]} 1.2.0 build ${BUILD_NUMBER}\""
                }
            }
        }
    }

} catch (e) {
    currentBuild.result = "FAILED"
    node ("asha-all") {
        sh "/usr/bin/curl -x socks5://proxyuser:secure@94.177.216.245:777 -X POST \"https://api.telegram.org/bot1018975900:AAFTH5MxjrzpSi-v0ivkw2TYmNIQLYf_ak0/sendMessage\" -d \"chat_id=-1001393370950&parse_mode=HTML&text=Сборка сломалась ${allJob[0]}/${allJob[1]}/${allJob[2]} 1.2.0 build ${BUILD_NUMBER}\""
    }
    throw e
}

