package ru.datana.steel.plc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import ru.datana.steel.plc.config.AppConst;
import ru.datana.steel.plc.db.CallDbService;
import ru.datana.steel.plc.util.ExtSpringProfileUtil;

import java.util.Arrays;

@Slf4j
@SpringBootApplication
@Profile(AppConst.DB_DEV_POSTGRES_PROFILE)
public class DatanaPlcClientApp implements CommandLineRunner {
    @Autowired
    CallDbService callDbService;


    public static void main(String[] args) throws Exception {
        ExtSpringProfileUtil.extConfigure(AppConst.DB_DEV_POSTGRES_PROFILE, AppConst.EXT_CLIENT_YAML);
        SpringApplication app = new SpringApplication(DatanaPlcClientApp.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }


    @Override
    public void run(String... args) {
        log.info(AppConst.APP_LOG_PREFIX + "================ Запуск Клиента  ================. Аргументы = " + Arrays.toString(args));
        try {

            callDbService.dbLoad();

        } catch (Exception ex) {
            log.error(AppConst.ERROR_LOG_PREFIX + " Ошибка в программе", ex);
        }
        log.info(AppConst.APP_LOG_PREFIX + "********* Завершение программы *********");
    }


}

