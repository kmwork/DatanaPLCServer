FROM openjdk:13-alpine
RUN apk add mc zip unzip bash
ENV TZ=Europe/Moscow
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
COPY . /home/lin/apps/plc-bin
CMD ["shell", "./run-server-on-linux.sh"]

#ENTRYPOINT ["tail", "-f", "/dev/null"]
