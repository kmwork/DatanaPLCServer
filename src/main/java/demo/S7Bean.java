package demo;

import org.apache.camel.Body;
import org.apache.camel.Handler;

import java.io.IOException;

public class S7Bean {

    @Handler
    public String doMyLogic(@Body String body) throws IOException {
        return "[S7:DEMO] " + new S7Api().getDataFromS7();
    }

    public String setMyHeader() {
        return "Here's my header definition, whatever the logic is";
    }

}