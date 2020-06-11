FROM openjdk:13-alpine
RUN apk add mc zip unzip bash
ENV TZ=Europe/Moscow
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN mkdir plcServer
RUN cp ~/apps/plc-bin/ /plcServer/

WORKDIR /plcServer


#ENTRYPOINT ["tail", "-f", "/dev/null"]
