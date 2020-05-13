package ru.datana.steel.camel.config;

/**
 * Константы программы
 */
public class AppConst {
    public static final String SYS_DIR_PROP = "app.dir";
    public final static String RESOURCE_FILE_NAME = "/danata-version.properties";
    public static final String ERROR_LOG_PREFIX = "[App-Ошибка] ";
    public static final String RESUME_LOG_PREFIX = "[App-Итог] ";
    public static final String APP_LOG_PREFIX = "[App-Danata] ";
    public static final int S7CONNECTOR_PORT_DEFAULT = 102;
    public static final Integer JSON_SUCCESS_CODE = 1;
    public static final Integer JSON_ERROR_CODE = 0;
    public final static String EXT_SERVER_YAML = "application.yaml";
    public static final String SERVER_PROFILE = "server";
    public static final long MIN_SLEEP_MS = 100;
    public static final long SLEEP_FUTURE_MS = 300;
}
