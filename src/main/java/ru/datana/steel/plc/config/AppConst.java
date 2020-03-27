package ru.datana.steel.plc.config;

/**
 * Константы программы
 */
public class AppConst {

    public static final String CONF_FILE_NAME = "datana_siemens.properties";
    public static final String SYS_DIR_PROP = "app.dir";
    public static final String ENCODING = "UTF8";


    public static final String SUCCESS_LOG_PREFIX = "[App-Успешно] ";
    public static final String ERROR_LOG_PREFIX = "[App-Ошибка] ";
    public static final String APP_LOG_PREFIX = "[App-Danata] ";
    public static final int S7CONNECTOR_PORT_DEFAULT = 102;
    public static final int TRY_S7CONTROLLER_READ_OF_COUNT = 3;

    public static final long S7_SLEEP_MS = 500;
    public static final Integer JSON_SUCCESS_CODE = 1;
    public static final Integer JSON_ERROR_CODE = 0;
}
