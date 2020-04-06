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


    public final static String EXT_SERVER_YAML = "application-server.yaml";
    public final static String EXT_CLIENT_YAML = "application-dev_client.yaml";


    public static final String S7_ROOT_CONFIG_FILE_NAME = "config-plc.xml";

    public static final String DB_DEV_POSTGRES_PROFILE = "dev_client";
    public static final String SERVER_PROFILE = "server";
    public static final String DB_REMOTE_POSTGRES_PROFILE = "remote_postgres";
    public static final String JSON_PREFIX_SENSOR = "Sensor";
    public static final String JSON_PREFIX_META_INFO = "MetaInfo";
}
