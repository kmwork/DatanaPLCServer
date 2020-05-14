package ru.datana.steel.camel.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.datana.steel.camel.model.json.request.JsonRootSensorRequest;
import ru.datana.steel.camel.util.AppException;

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
     * Передать датчики по Json при POST запросе
     *
     * @param fromJson
     * @return
     * @throws JsonProcessingException
     * @throws AppException
     */
    String getData(JsonRootSensorRequest fromJson) throws AppException;
}
