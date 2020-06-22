FROM openjdk:13-alpine
LABEL version="2.0"
ENV TZ=Europe/Moscow
WORKDIR /app_plc
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
COPY target/plc-server.jar .
COPY plcConfig/application-server.yaml .
COPY plcConfig/plc-meta-response-example.json .
ENTRYPOINT ["java", "-Dapp.dir=/app_plc", "-Dfile.encoding=UTF8", "-jar", "plc-server.jar"]
