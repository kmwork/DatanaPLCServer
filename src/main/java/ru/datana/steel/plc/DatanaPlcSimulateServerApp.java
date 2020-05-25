package ru.datana.steel.plc;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.EnableJms;
import ru.datana.steel.plc.config.AppConst;
import ru.datana.steel.plc.config.AsyncClientConfig;
import ru.datana.steel.plc.config.JmsProperties;
import ru.datana.steel.plc.config.RestSpringConfig;
import ru.datana.steel.plc.jms.PlcJmsProducer;
import ru.datana.steel.plc.model.json.response.JsonRootSensorResponse;
import ru.datana.steel.plc.model.json.response.JsonSensorResponse;
import ru.datana.steel.plc.util.AppException;
import ru.datana.steel.plc.util.ExtSpringProfileUtil;
import ru.datana.steel.plc.util.TimeUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * PLC Proxy Client -- клиент для работы со шлюзом датчиков
 * Тех задание: https://conf.dds.lanit.ru/display/NIOKR/PLC+Proxy+Client
 */
@Slf4j
@SpringBootApplication(
        exclude = {
                ServletWebServerFactoryAutoConfiguration.class,
                WebMvcAutoConfiguration.class})
@EnableJms
@Profile(AppConst.CLIENT_PROFILE)
public class DatanaPlcSimulateServerApp implements CommandLineRunner {


    @Autowired
    private RestSpringConfig restSpringConfig;

    @Value("${datana.sleep-ms}")
    private Long sleepMS;


    @Value("${datana.sensor-count}")
    private Integer sensorCount;

    @Value("${datana.loop-count}")
    private Long loopCount;

    private long sensorIndex;


    @Autowired
    private PlcJmsProducer jmsProducer;

    @Autowired
    private AsyncClientConfig asyncClientConfig;

    @Autowired
    private JmsProperties jmsProperties;

    public static void main(String[] args) {
        String fileName = System.getProperty(AppConst.FILE_YAML_PROP);
        if (StringUtils.isEmpty(fileName)) {
            log.error(AppConst.APP_LOG_PREFIX + "Профиль клиента не указан по свойству =  " + AppConst.FILE_YAML_PROP);
            System.exit(-110);
        }
        ExtSpringProfileUtil.extConfigure(AppConst.CLIENT_PROFILE, fileName);
        SpringApplication app = new SpringApplication(DatanaPlcSimulateServerApp.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
        System.exit(0);
    }


    @Override
    public void run(String... args) {
        log.info(AppConst.APP_LOG_PREFIX + "================ Запуск Клиента  ================. Аргументы = " + Arrays.toString(args));
        try {
            boolean makeSleepSQL = false;
            boolean success = false;
            JsonRootSensorResponse responseJson = genResponse();


            boolean infinityLoop = loopCount < 0;
            for (long index = 0; index < loopCount || infinityLoop; index++) {
                doOneRequest(responseJson, index);
            }

        } catch (Exception ex) {
            log.error(AppConst.ERROR_LOG_PREFIX + " Ошибка в программе", ex);
        }
        log.info(AppConst.APP_LOG_PREFIX + "********* Завершение программы *********");
    }

    private JsonRootSensorResponse genResponse() {

        JsonRootSensorResponse r = new JsonRootSensorResponse();

        List<JsonSensorResponse> sensors = new ArrayList(sensorCount);
        for (int i = 0; i < sensorCount; i++) {

            JsonSensorResponse ss = new JsonSensorResponse();
            ss.setId(sensorIndex);
            ss.setControllerDatetime(LocalDateTime.now());
            double d = Math.random() * 10000;
            BigDecimal decimal = BigDecimal.valueOf(Double.toString(d));
            ss.setData(decimal);
            ss.setStatus(0);
            sensors.add(ss);
            sensorIndex++;
        }

        r.setResponse(sensors);
        return r;
    }

    private void doOneRequest(JsonRootSensorResponse rootJson, long index) throws AppException, InterruptedException {
        long statTime = System.nanoTime();
        long step = index + 1;
        String prefixLog = "[Шаг: " + step + "] ";
        log.info(prefixLog);
        int threadCountMax = asyncClientConfig.getThreadCountMax();

        String formattedFromJson = restSpringConfig.toJson(prefixLog + " [Request] ", rootJson);
        for (int i = 0; i < threadCountMax; i++)
            jmsProducer.send("PlcRequest, Index = " + i, jmsProperties.getRequestQueue(), formattedFromJson);

        TimeUtil.doSleep(sleepMS, prefixLog);

        long endTime = System.nanoTime();
        long deltaNano = endTime - statTime;
        log.info(AppConst.RESUME_LOG_PREFIX + "Ушло времени за один запрос = {}", TimeUtil.formatTimeAsNano(deltaNano));
    }


}

