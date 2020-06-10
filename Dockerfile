FROM openjdk:13-alpine as plcServer
RUN apk add mc zip unzip maven bash
ENV TZ=Europe/Moscow
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
COPY ./tools /tools
WORKDIR /tools
RUN mvn clean compile package spring-boot:repackage -P plcServer


FROM openjdk:13-alpine as plcClient
RUN apk add mc zip unzip maven bash
ENV TZ=Europe/Moscow
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
COPY ./tools /tools
WORKDIR /tools
RUN mvn clean compile package spring-boot:repackage -P plcClient
RUN ls /tools/target




FROM openjdk:13-alpine
RUN apk add mc zip unzip bash
ENV TZ=Europe/Moscow
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY --from=plcServer /tools/target /tools/server
COPY --from=plcClient /tools/target /tools/client

COPY ./tools/etc /tools/server/etc
COPY ./tools/etc /tools/client/etc

#WORKDIR /tools/server
WORKDIR /tools/client


#ENTRYPOINT ["tail", "-f", "/dev/null"]
