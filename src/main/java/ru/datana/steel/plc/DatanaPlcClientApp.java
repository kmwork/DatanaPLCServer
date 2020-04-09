package ru.datana.steel.plc;

import feign.FeignException;
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
import ru.datana.steel.plc.config.AppConst;
import ru.datana.steel.plc.config.AppVersion;
import ru.datana.steel.plc.config.RestSpringConfig;
import ru.datana.steel.plc.db.CallDbService;
import ru.datana.steel.plc.model.json.request.JsonRootSensorRequest;
import ru.datana.steel.plc.rest.client.RestClientWebService;
import ru.datana.steel.plc.util.ExtSpringProfileUtil;
import ru.datana.steel.plc.util.TimeUtil;

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
    private CallDbService callDbService;

    @Autowired
    private RestClientWebService clientWebService;

    @Autowired
    private RestSpringConfig restSpringConfig;

    @Value("${datana.plc-server.sleep-ms}")
    private Long sleepMS;

    @Value("${datana.plc-server.loop-count}")
    private Long loopCount;

    @Value("${datana.plc-server.sleep-on-fatal-error}")
    private long sleepOnFatalError;

    public static void main(String[] args) {
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
            String serverVersion = null;
            do {
                try {
                    serverVersion = clientWebService.getVersion();
                } catch (FeignException ex) {
                    log.warn(AppConst.ERROR_LOG_PREFIX + "PLC-Server (Datana) не доступен: " + ex.getLocalizedMessage());
                    doSleep(sleepOnFatalError, "Ожидание запуска сервера: PLC-Шлюза");
                }
            } while (serverVersion == null);

            log.info("[Поиск сервера] сервер пропинговался, serverVersion = " + serverVersion);
            if (!AppVersion.getDatanaAppVersion().equalsIgnoreCase(serverVersion)) {
                log.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                log.error("Коллизия версий Клиент-Сервер: версия сервера = {}, версия клиента = {}", serverVersion, AppVersion.getDatanaAppVersion());
                log.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
            boolean makeSleepSQL = false;
            boolean success = false;
            JsonRootSensorRequest rootJson = null;
            do {
                try {
                    if (makeSleepSQL) {
                        doSleep(sleepOnFatalError, "Ждем пока починят хранимку GET");
                    } else
                        makeSleepSQL = true;
                    String tempJms = callDbService.dbGet();
                    rootJson = restSpringConfig.parseValue(tempJms, JsonRootSensorRequest.class);
                    success = rootJson != null && rootJson.getStatus() == 1;
                } catch (Exception e) {
                    log.error(AppConst.ERROR_LOG_PREFIX + "ошибка при работе хранимки get", e);

                }
            } while (!success);

            if (rootJson.getTimeout() != null)
                sleepMS = rootJson.getTimeout();

            rootJson.setStatus(null);
            rootJson.setTimeout(null);

            boolean infinityLoop = loopCount < 0;
            for (long index = 0; index < loopCount || infinityLoop; index++) {
                long statTime = System.nanoTime();
                long step = index + 1;
                String prefixLog = "[Шаг: " + step + "]";
                log.info(prefixLog);
                changeIDCodes(step, rootJson);

                String formattedFromJson = restSpringConfig.toJson(prefixLog + " [Request] ", rootJson);
                String toJson = clientWebService.getData(formattedFromJson);
                String resultFromJson = restSpringConfig.formatBeautyJson(prefixLog + " [Response] ", toJson);
                String saveJson = callDbService.dbSave(resultFromJson);
                restSpringConfig.formatBeautyJson(prefixLog + " [Save:RESULT] ", saveJson);
                long endTime = System.nanoTime();
                long deltaNano = endTime - statTime;
                log.info(AppConst.RESUME_LOG_PREFIX + "Ушло времени за один запрос = {}", TimeUtil.formatTimeAsNano(deltaNano));
                doSleep(sleepMS, "Перекур на цикл");
            }

        } catch (Exception ex) {
            log.error(AppConst.ERROR_LOG_PREFIX + " Ошибка в программе", ex);
        }
        log.info(AppConst.APP_LOG_PREFIX + "********* Завершение программы *********");
    }

    private void doSleep(long time, String msg) throws InterruptedException {
        if (time < 100)
            log.warn("Запрещено ставить меньше задержки min = {}", AppConst.MIN_SLEEP_MS);
        time = Math.max(time, AppConst.MIN_SLEEP_MS);
        log.warn("[*** СОН Для клиента ***] " + msg + ", на время = " + time);
        Thread.sleep(time);
    }

    private void changeIDCodes(long step, JsonRootSensorRequest rootJson) {
        String uuid = UUID.randomUUID().toString();
        LocalDateTime time = LocalDateTime.now();
        rootJson.setRequestId(uuid);
        rootJson.setRequestDatetime(time);
        log.info("[changeIDCodes] [Шаг: {}] Создан ID = {} с временем = {}", step, uuid, time);
        log.info("[Запрос] rootJson = " + rootJson);
    }


}

