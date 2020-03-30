package ru.datana.steel.plc.util;

import lombok.Getter;

public class DatanaJsonHelper {

    @Getter
    private final static DatanaJsonHelper instance = new DatanaJsonHelper();
    private int requestCount = 0;
    private int responseCount = 0;

    private DatanaJsonHelper() {

    }

    public String genRequestId() {
        requestCount++;
        return "Request:" + System.nanoTime() + ":Req-Index:" + requestCount;
    }

    public String genResponseId() {
        responseCount++;
        return "Response:" + System.nanoTime() + ":Res-Index:" + responseCount;
    }
}
