FROM openjdk:13-alpine
RUN apk add mc zip unzip bash
ENV TZ=Europe/Moscow
RUN mkdir -p /app_plc
WORKDIR /app_plc
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ADD target/plc-server.jar /app_plc/plc-server.jar
ADD etc/application-server.yaml /app_plc/application-server.yaml
ADD etc/plc-meta-response-example.json /app_plc/plc-meta-response-example.json
ENTRYPOINT ["java", "-Dapp.dir=/app_plc", "-Dfile.encoding=UTF8", "-jar", "plc-server.jar"]
