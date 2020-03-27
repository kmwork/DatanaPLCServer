package ru.datana.steel.plc.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ru.datana.steel.plc.model.json.meta.JsonMetaRootController;
import ru.datana.steel.plc.model.json.request.JsonRootRequest;
import ru.datana.steel.plc.model.json.response.JsonRootResponse;
import ru.datana.steel.plc.s7controller.S7GithubExecutor;

import java.io.File;

@Slf4j
public class JsonParserUtil {

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        //JSON file to Java object
        JsonRootRequest jsonRootRequest = mapper.readValue(new File("/home/lin/work-lanit/Datata-Kafla-Gateway-K7/src/main/resources/json/request-example.json"), JsonRootRequest.class);
        log.info("[JSON-Parser] jsonRootRequest = " + jsonRootRequest);


        JsonMetaRootController jsonRootMetaResponse = mapper.readValue(new File("/home/lin/work-lanit/Datata-Kafla-Gateway-K7/src/main/resources/json/plc-meta-response-example.json"), JsonMetaRootController.class);

        S7GithubExecutor s7 = new S7GithubExecutor();
        s7.init(jsonRootMetaResponse);
        JsonRootResponse jsonRootResponse = s7.run(jsonRootRequest);
        //JsonRootResponse jsonRootResponse = mapper.readValue(new File("/home/lin/work-lanit/Datata-Kafla-Gateway-K7/src/main/resources/json/response-example.json"), JsonRootResponse.class);
        log.info("[JSON-Parser] jsonRootResponse = " + jsonRootResponse);
    }
}
