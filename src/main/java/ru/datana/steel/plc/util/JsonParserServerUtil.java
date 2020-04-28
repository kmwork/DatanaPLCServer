package ru.datana.steel.plc.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.datana.steel.plc.model.json.meta.JsonMetaRootController;

/**
 * Парсер мета информации о контроллерах для сервера
 */
@Slf4j
public class JsonParserServerUtil {
    @Getter
    private final static JsonParserServerUtil instance = new JsonParserServerUtil();

    private JsonParserUtil<JsonMetaRootController> parserUtil = new JsonParserUtil<JsonMetaRootController>("plc-meta-response-example.json");

    private JsonParserServerUtil() {
    }

    public JsonMetaRootController loadJsonMetaRootController() throws AppException {
        return parserUtil.loadJson(JsonMetaRootController.class);
    }
}
