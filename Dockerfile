FROM openjdk:13-alpine
RUN apk add mc zip unzip bash
ENV TZ=Europe/Moscow
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
COPY . /home/lin/apps/plc-bin
### CMD ["bash", "run-server-on-linux.sh"]
CMD ["java", "-Dapp.dir=.", "-Dfile.encoding=UTF8", "-jar", "plc-server.jar"]
###java -Dapp.dir="/home/lin/apps/plc-bin" -Dfile.encoding=UTF8 -jar plc-server.jar
#ENTRYPOINT ["tail", "-f", "/dev/null"]
