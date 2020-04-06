package ru.datana.steel.plc;

import lombok.extern.slf4j.Slf4j;
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
import ru.datana.steel.plc.db.CallDbService;
import ru.datana.steel.plc.rest.client.RestClientWebService;
import ru.datana.steel.plc.util.ExtSpringProfileUtil;

import java.util.Arrays;

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

    @Value("${datana.plc-server.sleep-ms}")
    protected Long sleepMS;

    @Value("${datana.plc-server.loop-count}")
    protected Integer loopCount;

    public static void main(String[] args) throws Exception {
        ExtSpringProfileUtil.extConfigure(AppConst.DB_DEV_POSTGRES_PROFILE, AppConst.EXT_REMOTE_CLIENT_YAML);
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

            for (int index = 0; index < loopCount; index++) {
                log.info("[Шаг] step = " + (index + 1));
                String fromJson = callDbService.dbGet();
                String toJson = clientWebService.getData(fromJson);
                callDbService.dbSave(toJson);
                Thread.sleep(sleepMS);
            }

        } catch (Exception ex) {
            log.error(AppConst.ERROR_LOG_PREFIX + " Ошибка в программе", ex);
        }
        log.info(AppConst.APP_LOG_PREFIX + "********* Завершение программы *********");
    }


}

