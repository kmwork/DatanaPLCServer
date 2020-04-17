package ru.datana.steel.plc.db;

import com.github.wenerme.postjava.hibernate.dialect.PostgreSQLJsonDialect;

import java.sql.Types;

/**
 * Диалект по работе PostgreSQL при JSON типах
 *
 * @author timfulmer
 */
public class DatanaJsonPostgreSQLDialect extends PostgreSQLJsonDialect {

    public DatanaJsonPostgreSQLDialect() {
        super();
        this.registerColumnType(Types.OTHER, "jsonb");
    }
}
