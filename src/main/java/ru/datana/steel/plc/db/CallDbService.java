package ru.datana.steel.plc.db;

import java.sql.SQLException;

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
     * @param fromJson
     * @return
     * @throws SQLException
     */
    String dbSave(String fromJson);
}
