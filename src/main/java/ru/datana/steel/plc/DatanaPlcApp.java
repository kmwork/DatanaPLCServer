package ru.datana.steel.plc;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.datana.steel.plc.config.AppConst;
import ru.datana.steel.plc.kafka.DanataPlcServerKafkaMessageProducer;
import ru.datana.steel.plc.kafka.DatanaKafkaListener;

import java.io.File;
import java.util.Properties;

@SpringBootApplication
@Slf4j
public class DatanaPlcApp {

    public static void main(String[] args) throws Exception {
        extConfigure();
        ConfigurableApplicationContext context = SpringApplication.run(DatanaPlcApp.class, args);

        DanataPlcServerKafkaMessageProducer producer = context.getBean(DanataPlcServerKafkaMessageProducer.class);
        DatanaKafkaListener listener = context.getBean(DatanaKafkaListener.class);

        producer.sendMessage("Hello, World!");

        for (int i = 0; i < 5; i++) {
            producer.sendMessage("Hello To Partioned Topic!");
        }
        context.close();
    }


    private static void extConfigure() {
        String configLocation = System.getProperty(AppConst.SYS_DIR_PROP); //get the default config directory location
        if (StringUtils.isEmpty(AppConst.SYS_DIR_PROP)) {
            log.error(AppConst.APP_LOG_PREFIX + "Путь к настройкам не указан по java.options по имени свойства =  " + AppConst.SYS_DIR_PROP);
            System.exit(-100);
        }

        String strProfile = System.getProperty(AppConst.SYS_PROFILE_PROP);
        if (StringUtils.isEmpty(strProfile)) {
            log.error(AppConst.APP_LOG_PREFIX + "Профиль не указан по java.options: " + AppConst.SYS_PROFILE_PROP);
            System.exit(-110);
        }

        String springProfile = AppConst.DB_DEV_POSTGRES_PROFILE;
        String springFile = AppConst.EXT_DEV_CONFIG_NAME;
        if (strProfile.equalsIgnoreCase(AppConst.DB_REMOTE_POSTGRES_PROFILE)) {
            springProfile = AppConst.DB_REMOTE_POSTGRES_PROFILE;
            springFile = AppConst.EXT_REMOTE_CONFIG_NAME;
            log.info("[Конфигурация] включет профиль удаленной базы данных: " + springFile);
        }
        File configPath = new File(configLocation, springFile);
        log.info(AppConst.APP_LOG_PREFIX + "[Config]: configPath = " + configPath);
        log.info(AppConst.APP_LOG_PREFIX + "Настройка приложения");

        if (configPath.exists()) {
            Properties props = System.getProperties();
            props.setProperty("spring.config.location", configPath.getAbsolutePath()); //set the config file to use
            props.setProperty("spring.profiles.active", springProfile);
            props.setProperty("java.awt.headless", "false");
            props.setProperty("file.encoding", "UTF8");
        } else {
            log.error(AppConst.ERROR_LOG_PREFIX + "Конфиг не найден по пути " + configPath.getAbsolutePath());
            System.exit(-200);
        }
    }


}
