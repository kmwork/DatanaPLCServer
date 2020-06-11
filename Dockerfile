FROM openjdk:13-alpine
RUN apk add mc zip unzip bash
ENV TZ=Europe/Moscow
RUN mkdir -p /app_plc
WORKDIR /app_plc
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
COPY target/plc-server.jar ./
COPY etc/application-server.yaml ./
COPY etc/plc-meta-response-example.json ./
ENTRYPOINT ["java", "-Dapp.dir=/app_plc", "-Dfile.encoding=UTF8", "-jar", "plc-server.jar"]
