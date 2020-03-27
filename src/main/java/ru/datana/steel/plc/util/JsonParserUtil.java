package ru.datana.steel.plc.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ru.datana.steel.plc.config.AppConst;
import ru.datana.steel.plc.model.json.meta.JsonMetaRootController;
import ru.datana.steel.plc.model.json.request.JsonRootRequest;
import ru.datana.steel.plc.model.json.response.JsonRootResponse;
import ru.datana.steel.plc.s7controller.S7GithubExecutor;

import java.io.File;

@Slf4j
public class JsonParserUtil {

    public static void main(String[] args) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

            String dir = System.getProperty(AppConst.SYS_DIR_PROP);
            if (StringUtils.isEmpty(dir)) {
                String strArgs = AppConst.SYS_DIR_PROP + " = '" + dir + "'";
                throw new AppException(TypeException.INVALID_USER_INPUT_DATA, "пустое значение", strArgs, null);
            }
            JsonRootRequest jsonRootRequest = mapper.readValue(new File(dir, "request-example.json"), JsonRootRequest.class);
            log.info("[JSON-Parser] jsonRootRequest = " + jsonRootRequest);


            JsonMetaRootController jsonRootMetaResponse = mapper.readValue(new File(dir, "plc-meta-response-example.json"), JsonMetaRootController.class);

            S7GithubExecutor s7 = new S7GithubExecutor();
            s7.init(jsonRootMetaResponse);
            JsonRootResponse jsonRootResponse = s7.run(jsonRootRequest);
            log.info("[JSON-Parser] jsonRootResponse = " + jsonRootResponse);
            log.info(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonRootResponse));
        } catch (Exception ex) {
            log.error("Ошибка в программе: ", ex);
        }
    }
}
