package ru.datana.steel.plc.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.datana.steel.plc.model.json.request.JsonRootSensorRequest;

/**
 * Парсер мета информации о контроллерах
 */
@Slf4j
public class JsonParserClientUtil {
    @Getter
    private final static ru.datana.steel.plc.util.JsonParserClientUtil instance = new ru.datana.steel.plc.util.JsonParserClientUtil();

    private JsonParserUtil<JsonRootSensorRequest> parserUtil = new JsonParserUtil<JsonRootSensorRequest>("plc-meta-request-example.json");

    private JsonParserClientUtil() {
    }

    public JsonRootSensorRequest loadJsonRequest() throws AppException {
        return parserUtil.loadJson(JsonRootSensorRequest.class);
    }
}
