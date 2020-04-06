package ru.datana.steel.plc.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ru.datana.steel.plc.config.AppConst;
import ru.datana.steel.plc.model.json.meta.JsonMetaRootController;

import java.io.File;
import java.io.IOException;

@Slf4j
public class JsonParserUtil {
    @Getter
    private final static JsonParserUtil instance = new JsonParserUtil();

    private final ObjectMapper mapper = new ObjectMapper();
    private String dir = System.getProperty(AppConst.SYS_DIR_PROP);
    private long prevLastModified = 0;
    private JsonMetaRootController prevJsonRootMetaResponse = null;

    private JsonParserUtil() {
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public JsonMetaRootController loadJsonMetaRootController() throws AppException {
        if (StringUtils.isEmpty(dir)) {
            String strArgs = AppConst.SYS_DIR_PROP + " = '" + dir + "'";
            throw new AppException(TypeException.INVALID_USER_INPUT_DATA, "пустое значение", strArgs, null);
        }

        File f = new File(dir, "plc-meta-response-example.json");
        try {

            JsonMetaRootController result;
            if (prevLastModified < f.lastModified() || prevJsonRootMetaResponse == null) {
                log.info("[JSON-Parser:Load-Meta] Чтение файла = " + f.getAbsoluteFile());
                result = mapper.readValue(f, JsonMetaRootController.class);
                prevLastModified = f.lastModified();
                prevJsonRootMetaResponse = result;
            } else
                result = prevJsonRootMetaResponse;
            log.info("[JSON-Parser:Load-Meta] result = " + result);
            //log.info(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonRootResponse));
            return result;
        } catch (IOException ex) {
            log.error("Ошибка в программе: ", ex);
            String strArgs = "File: " + f.getAbsoluteFile();
            throw new AppException(TypeException.INVALID_USER_INPUT_META_FILE, "Файл не смог прочитать", strArgs, ex);
        }

    }
}
