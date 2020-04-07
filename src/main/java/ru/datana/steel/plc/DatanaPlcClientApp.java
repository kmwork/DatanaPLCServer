package ru.datana.steel.plc;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.GenericApplicationContext;
import ru.datana.steel.plc.config.AppConst;
import ru.datana.steel.plc.config.RestSpringConfig;
import ru.datana.steel.plc.db.CallDbService;
import ru.datana.steel.plc.model.json.request.JsonRootSensorRequest;
import ru.datana.steel.plc.rest.client.RestClientWebService;
import ru.datana.steel.plc.util.ExtSpringProfileUtil;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

/**
 * PLC Proxy Client -- клиент для работы со шлюзом датчиков
 * Тех задание: https://conf.dds.lanit.ru/display/NIOKR/PLC+Proxy+Client
 */
@Slf4j
@SpringBootApplication(
        exclude = {
                ServletWebServerFactoryAutoConfiguration.class,
                WebMvcAutoConfiguration.class})
@EnableFeignClients
@Profile(AppConst.DB_DEV_POSTGRES_PROFILE)
public class DatanaPlcClientApp implements CommandLineRunner {
    @Autowired
    protected GenericApplicationContext context;

    @Autowired
    protected CallDbService callDbService;

    @Autowired
    protected RestClientWebService clientWebService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestSpringConfig restSpringConfig;

    @Value("${datana.plc-server.sleep-ms}")
    protected Long sleepMS;

    @Value("${datana.plc-server.loop-count}")
    protected Long loopCount;

    public static void main(String[] args) throws Exception {
        String fileName = System.getProperty(AppConst.FILE_YAML_PROP);
        if (StringUtils.isEmpty(fileName)) {
            log.error(AppConst.APP_LOG_PREFIX + "Профиль клиента не указан по свойству =  " + AppConst.FILE_YAML_PROP);
            System.exit(-110);
        }
        ExtSpringProfileUtil.extConfigure(AppConst.DB_DEV_POSTGRES_PROFILE, fileName);
        SpringApplication app = new SpringApplication(DatanaPlcClientApp.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }


    @Override
    public void run(String... args) {
        log.info(AppConst.APP_LOG_PREFIX + "================ Запуск Клиента  ================. Аргументы = " + Arrays.toString(args));
        try {

            String serverVersion = clientWebService.getVersion();
            log.info("[Поиск сервера] сервер пропинговался, serverVersion = " + serverVersion);
            String fromJsonеTemp = callDbService.dbGet();
            JsonRootSensorRequest rootJson = restSpringConfig.parseValue(fromJsonеTemp, JsonRootSensorRequest.class);

            if (rootJson.getTimeout() != null)
                sleepMS = rootJson.getTimeout();

            boolean infinityLoop = loopCount < 0;
            for (long index = 0; index < loopCount || infinityLoop; index++) {
                long step = index + 1;
                String prefixLog = "[Шаг: " + step + "]";
                log.info(prefixLog);
                changeIDCodes(step, rootJson);

                String formattedFromJson = restSpringConfig.toJson(prefixLog + " [Request] ", rootJson);
                String toJson = clientWebService.getData(formattedFromJson);
                String resultFromJson = restSpringConfig.formatBeautyJson(prefixLog + " [Response] ", toJson);
                String saveJson = callDbService.dbSave(resultFromJson);
                restSpringConfig.formatBeautyJson(prefixLog + " [Save:RESULT] ", saveJson);
                Thread.sleep(sleepMS);
            }

        } catch (Exception ex) {
            log.error(AppConst.ERROR_LOG_PREFIX + " Ошибка в программе", ex);
        }
        log.info(AppConst.APP_LOG_PREFIX + "********* Завершение программы *********");
    }

    private void changeIDCodes(long step, JsonRootSensorRequest rootJson) {
        String uuid = UUID.randomUUID().toString();
        LocalDateTime time = LocalDateTime.now();
        rootJson.setRequestId(uuid);
        rootJson.setRequestDatetime(time);
        log.info("[changeIDCodes] [Шаг: {}] Сгенерирован ID = {} с временем = ", step, uuid, time);
        log.info("[Запрос] rootJson = " + rootJson);
    }


}

