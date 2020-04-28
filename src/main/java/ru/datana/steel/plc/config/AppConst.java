package ru.datana.steel.plc.config;

/**
 * Константы программы
 */
public class AppConst {
    public static final String SYS_DIR_PROP = "app.dir";
    public final static String FILE_YAML_PROP = "app.config.file";
    public final static String RESOURCE_FILE_NAME = "/danata-version.properties";
    public static final String ERROR_LOG_PREFIX = "[App-Ошибка] ";
    public static final String RESUME_LOG_PREFIX = "[App-Итог] ";
    public static final String APP_LOG_PREFIX = "[App-Danata] ";
    public static final Integer JSON_SUCCESS_CODE = 1;
    public static final Integer JSON_ERROR_CODE = 0;
    public final static String EXT_SERVER_YAML = "application-server.yaml";
    public static final String CLIENT_PROFILE = "dev_client";
    public static final String SERVER_PROFILE = "server";
    public static final long MIN_SLEEP_MS = 100;
    public static final long SLEEP_FUTURE_MS = 300;
}
