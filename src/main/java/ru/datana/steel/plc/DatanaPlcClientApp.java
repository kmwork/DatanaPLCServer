package ru.datana.steel.plc;


import lombok.Getter;
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
import ru.datana.steel.plc.jms.PlcJmsClientListener;
import ru.datana.steel.plc.jms.PlcJmsProducer;
import ru.datana.steel.plc.model.json.request.JsonRootSensorRequest;
import ru.datana.steel.plc.util.AppException;
import ru.datana.steel.plc.util.ExtSpringProfileUtil;
import ru.datana.steel.plc.util.JsonParserClientUtil;
import ru.datana.steel.plc.util.TimeUtil;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

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
public class DatanaPlcClientApp implements CommandLineRunner {


    @Autowired
    private RestSpringConfig restSpringConfig;

    @Value("${datana.plc-server.sleep-ms}")
    private Long sleepMS;

    @Value("${datana.plc-server.loop-count}")
    private Long loopCount;

    @Value("${datana.plc-server.sleep-on-fatal-error}")
    private long sleepOnFatalError;

    private final AtomicInteger threadCount = new AtomicInteger();

    @Value("${datana.plc-client.async-timeout}")
    @Getter
    private int asyncMS;


    @Autowired
    private PlcJmsProducer jmsProducer;

    @Autowired
    private AsyncClientConfig asyncClientConfig;

    @Autowired
    private JmsProperties jmsProperties;

    @Autowired
    private PlcJmsClientListener plcJmsClientListener;

    public static void main(String[] args) {
        String fileName = System.getProperty(AppConst.FILE_YAML_PROP);
        if (StringUtils.isEmpty(fileName)) {
            log.error(AppConst.APP_LOG_PREFIX + "Профиль клиента не указан по свойству =  " + AppConst.FILE_YAML_PROP);
            System.exit(-110);
        }
        ExtSpringProfileUtil.extConfigure(AppConst.CLIENT_PROFILE, fileName);
        SpringApplication app = new SpringApplication(DatanaPlcClientApp.class);
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
            JsonParserClientUtil clientUtil = JsonParserClientUtil.getInstance();
            JsonRootSensorRequest rootJson = clientUtil.loadJsonRequest();
            if (rootJson.getTimeout() != null)
                sleepMS = rootJson.getTimeout();

            rootJson.setStatus(null);
            rootJson.setTimeout(null);

            boolean infinityLoop = loopCount < 0;
            for (long index = 0; index < loopCount || infinityLoop; index++) {
                doOneRequest(rootJson, index);
            }

        } catch (Exception ex) {
            log.error(AppConst.ERROR_LOG_PREFIX + " Ошибка в программе", ex);
        }
        log.info(AppConst.APP_LOG_PREFIX + "********* Завершение программы *********");
    }

    private void doOneRequest(JsonRootSensorRequest rootJson, long index) throws AppException, InterruptedException {
        long statTime = System.nanoTime();
        long step = index + 1;
        String prefixLog = "[Шаг: " + step + "] ";
        log.info(prefixLog);
        changeIDCodes(step, rootJson);

        String formattedFromJson = restSpringConfig.toJson(prefixLog + " [Request] ", rootJson);
        jmsProducer.send("PlcRequest", jmsProperties.getRequestQueue(), formattedFromJson);

        threadCount.set(asyncClientConfig.getThreadCountMax());

        int threadCountMax = asyncClientConfig.getThreadCountMax();

        threadCount.set(threadCountMax);
        while (plcJmsClientListener.getCounter().get() < threadCountMax)
            TimeUtil.doSleep(asyncMS, "Ожидание потоков Async: " + threadCount.get());

        long endTime = System.nanoTime();
        long deltaNano = endTime - statTime;
        log.info(AppConst.RESUME_LOG_PREFIX + "Ушло времени за один запрос = {}", TimeUtil.formatTimeAsNano(deltaNano));
    }

    private void changeIDCodes(long step, JsonRootSensorRequest rootJson) {
        String uuid = UUID.randomUUID().toString();
        LocalDateTime time = LocalDateTime.now();
        rootJson.setRequestId(uuid);
        rootJson.setRequestDatetime(time);

        if (log.isDebugEnabled())
            log.debug("[changeIDCodes] [Шаг: {}] Создан ID = {} с временем = {}", step, uuid, time);

        if (log.isTraceEnabled()) {
            log.trace("[Запрос] rootJson = " + rootJson);
        }
    }


}

