package ru.datana.steel.plc.util;

import lombok.Getter;

public class DatanaJsonHelper {

    @Getter
    private final static DatanaJsonHelper instance = new DatanaJsonHelper();
    private int requestCount = 0;
    private int responseCount = 0;

    private DatanaJsonHelper() {

    }

    public String genRequestId(String prefix) {
        requestCount++;
        return "Request[" + prefix + "]:" + System.nanoTime() + ":Req-Index:" + requestCount;
    }

    public String genResponseId(String prefix) {
        responseCount++;
        return "Response[" + prefix + "]:" + System.nanoTime() + ":Res-Index:" + responseCount;
    }
}
