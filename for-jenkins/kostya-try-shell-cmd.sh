#!/bin/bash -xe
echo "CHANGE_AUTHOR_DISPLAY_NAME = $CHANGE_AUTHOR_DISPLAY_NAME"
echo "BUILD_NUMBER = $BUILD_NUMBER"
echo "GIT_COMMITTER_NAME = $GIT_COMMITTER_NAME"


DatanaAuthor = $[git show -s --pretty=\"%an <%ae>\" ${GIT_COMMIT}]
echo $DatanaAuthor


curl -x socks5://proxyuser:secure@94.177.216.245:777 -X POST "https://api.telegram.org/bot1180854473:AAG1BHnbcM4oRRZW2-DKbZMYD2WqkDtUesU/sendMessage?chat_id=-1001325011128&parse_mode=HTML&text=Собрал. CHANGE_AUTHOR_DISPLAY_NAME = $CHANGE_AUTHOR_DISPLAY_NAME. BUILD_NUMBER = $BUILD_NUMBER. GIT_COMMITTER_NAME = $GIT_COMMITTER_NAME"


