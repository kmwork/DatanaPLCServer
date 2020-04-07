package ru.datana.steel.plc.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.datana.steel.plc.model.json.request.JsonRootSensorRequest;
import ru.datana.steel.plc.util.AppException;

/**
 * Описание сервиса REST
 */
public interface S7RestApi {
    /**
     * Вернуть версию сервера по GET запросу
     *
     * @return
     */
    String getVersion();

    /**
     * Передать дачики по Json при POST запросе
     *
     * @param fromJson
     * @return
     * @throws JsonProcessingException
     * @throws AppException
     */
    String getData(JsonRootSensorRequest fromJson) throws JsonProcessingException, AppException;
}
