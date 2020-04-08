package ru.datana.steel.plc.config;

/**
 * Константы программы
 */
public class AppConst {
    public static final String SYS_DIR_PROP = "app.dir";
    public final static String FILE_YAML_PROP = "app.config.file";
    public final static String RESOURCE_FILE_NAME = "/danata-version.properties";
    public static final String ERROR_LOG_PREFIX = "[App-Ошибка] ";
    public static final String APP_LOG_PREFIX = "[App-Danata] ";
    public static final int S7CONNECTOR_PORT_DEFAULT = 102;
    public static final int TRY_S7CONTROLLER_READ_OF_COUNT = 3;
    public static final long S7_SLEEP_MS = 500;
    public static final Integer JSON_SUCCESS_CODE = 1;
    public static final Integer JSON_ERROR_CODE = 0;
    public final static String EXT_SERVER_YAML = "application-server.yaml";
    public static final String DB_DEV_POSTGRES_PROFILE = "dev_client";
    public static final String SERVER_PROFILE = "server";
}
