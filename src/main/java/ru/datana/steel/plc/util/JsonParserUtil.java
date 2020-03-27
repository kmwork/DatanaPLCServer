package ru.datana.steel.plc.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.datana.steel.plc.model.json.request.JsonRootRequest;

import java.io.File;
import java.io.IOException;

public class JsonParserUtil {

    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        //JSON file to Java object
        JsonRootRequest obj = mapper.readValue(new File("/home/lin/work-lanit/Datata-Kafla-Gateway-K7/src/main/resources/json/request-example.json"), JsonRootRequest.class);

    }
}
