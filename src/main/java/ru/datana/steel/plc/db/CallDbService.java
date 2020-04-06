package ru.datana.steel.plc.db;

import java.sql.SQLException;

public interface CallDbService {
    String dbGet() throws SQLException;

    String dbSave(String fromJson) throws SQLException;
}
