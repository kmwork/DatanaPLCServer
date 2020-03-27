package ru.datana.steel.plc.util;

import lombok.extern.slf4j.Slf4j;
import ru.datana.steel.plc.config.AppConst;

import java.io.*;
import java.util.Properties;

/**
 * Чтение конфига datana_siemens.properties
 */
@Slf4j
public class LanitFileUtils {

    public static Properties readDataConfig() throws AppException {
        String dirConf = ValueParser.readPropAsText(System.getProperties(), AppConst.SYS_DIR_PROP, false);

        File f = new File(dirConf, AppConst.CONF_FILE_NAME);
        if (!f.isFile() || !f.exists()) {
            log.error("Файл не найден: " + f.getAbsolutePath());
            System.exit(-100);
        }

        Properties p = new Properties();
        try (Reader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(f), AppConts.ENCODING));) {
            p.load(fileReader);
        } catch (IOException ex) {
            log.error("Не смог прочитать файл: " + f.getAbsolutePath(), ex);
            System.exit(-200);
        }
        log.info("[CONFIG]" + p);

        return p;

    }
}
