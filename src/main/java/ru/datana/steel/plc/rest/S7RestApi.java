package ru.datana.steel.plc.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.datana.steel.plc.model.json.request.JsonRootSensorRequest;

public interface S7RestApi {
    String getVersion();

    String getData(JsonRootSensorRequest fromJson) throws JsonProcessingException;
}
