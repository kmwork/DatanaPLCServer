package ru.datana.steel.camel.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.datana.steel.camel.model.json.request.JsonRootSensorRequest;

/**
 * Парсер мета информации о контроллерах
 */
@Slf4j
public class JsonParserClientUtil {
    @Getter
    private final static JsonParserClientUtil instance = new JsonParserClientUtil();

    private JsonParserUtil<JsonRootSensorRequest> parserUtil = new JsonParserUtil<>("plc-meta-request-example.json");

    private JsonParserClientUtil() {
    }

    public JsonRootSensorRequest loadJsonRequest() throws AppException {
        return parserUtil.loadJson(JsonRootSensorRequest.class);
    }
}
