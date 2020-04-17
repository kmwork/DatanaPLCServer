package ru.datana.steel.plc.db;

import ru.datana.steel.plc.model.json.request.JsonRootSensorRequest;
import ru.datana.steel.plc.util.AppException;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * API для вызова хранимок на PostgreSQL
 */
public interface CallDbService {
    /**
     * Получить JSON-строку для задания выборки данных с датчиков
     *
     * @return
     * @throws SQLException
     */
    String dbGet();

    /**
     * Сохранить через передачу JSON строки с данными датчиков в базу данных Postgresql
     *
     * @throws SQLException
     */
    String dbSave(JsonRootSensorRequest fromJson, int threadCountMax, int threadCurrent) throws SQLException;

    void saveAsync(String prefixLog, JsonRootSensorRequest resultFromJson, int poolIndex, int threadCountMax, AtomicInteger threadCount) throws AppException, InterruptedException;
}
