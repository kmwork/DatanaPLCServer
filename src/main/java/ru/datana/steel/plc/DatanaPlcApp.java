package ru.datana.steel.plc;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ru.datana.steel.plc.model.json.request.JsonRootRequest;
import ru.datana.steel.plc.model.json.response.JsonRootResponse;

import java.io.File;
import java.io.IOException;

@Slf4j
public class DatanaPlcApp {

    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        //JSON file to Java object
        JsonRootRequest jsonRootRequest = mapper.readValue(new File("/home/lin/work-lanit/Datata-Kafla-Gateway-K7/src/main/resources/json/request-example.json"), JsonRootRequest.class);
        log.info("[JSON-Parser] jsonRootRequest = " + jsonRootRequest);

        JsonRootResponse jsonRootResponse = mapper.readValue(new File("/home/lin/work-lanit/Datata-Kafla-Gateway-K7/src/main/resources/json/response-example.json"), JsonRootResponse.class);
        log.info("[JSON-Parser] jsonRootResponse = " + jsonRootResponse);
    }
}
