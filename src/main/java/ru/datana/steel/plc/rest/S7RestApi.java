package ru.datana.steel.plc.rest;

import ru.datana.steel.plc.model.json.request.JsonRootSensorRequest;
import ru.datana.steel.plc.model.json.response.JsonRootSensorResponse;

public interface S7RestApi {
    String getVersion();

    JsonRootSensorResponse getData(JsonRootSensorRequest fromJson);
}
