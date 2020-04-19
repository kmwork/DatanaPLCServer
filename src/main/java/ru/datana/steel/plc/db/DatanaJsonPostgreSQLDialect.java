package ru.datana.steel.plc.db;


import org.hibernate.dialect.PostgreSQL10Dialect;

import java.sql.Types;

/**
 * Диалект по работе PostgreSQL при JSON типах
 *
 * @author timfulmer
 */
public class DatanaJsonPostgreSQLDialect extends PostgreSQL10Dialect {

    public DatanaJsonPostgreSQLDialect() {
        super();
        this.registerColumnType(Types.OTHER, "jsonb");
    }
}
