package ru.datana.steel.plc.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ru.datana.steel.plc.config.AppConst;

import java.io.File;
import java.util.Properties;

@Slf4j
public class ExtSpringProfileUtil {

    public static void extConfigure(String springProfile, String springFile) {
        String configLocation = System.getProperty(AppConst.SYS_DIR_PROP); //get the default config directory location
        if (StringUtils.isEmpty(AppConst.SYS_DIR_PROP)) {
            log.error(AppConst.APP_LOG_PREFIX + "Путь к настройкам не указан по java.options по имени свойства =  " + AppConst.SYS_DIR_PROP);
            System.exit(-100);
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

    public static void extConfigurClient(String springProfile) {
        String configLocation = System.getProperty(AppConst.SYS_DIR_PROP); //get the default config directory location
        if (StringUtils.isEmpty(AppConst.SYS_DIR_PROP)) {
            log.error(AppConst.APP_LOG_PREFIX + "Путь к настройкам не указан по java.options по имени свойства =  " + AppConst.SYS_DIR_PROP);
            System.exit(-100);
        }

        File configPath = new File(configLocation, springFile);
        app.config.file
    }
}
