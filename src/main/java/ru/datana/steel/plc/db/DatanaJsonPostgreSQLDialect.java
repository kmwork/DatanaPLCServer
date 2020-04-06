package ru.datana.steel.plc.db;

import org.hibernate.dialect.PostgreSQL10Dialect;

import java.sql.Types;

/**
 * Wrap default PostgreSQL9Dialect with 'json' type.
 *
 * @author timfulmer
 */
public class DatanaJsonPostgreSQLDialect extends PostgreSQL10Dialect {

    public DatanaJsonPostgreSQLDialect() {
        super();
        this.registerColumnType(Types.JAVA_OBJECT, "jsonb");
        this.registerColumnType(Types.OTHER, "jsonb");
        this.registerHibernateType(Types.JAVA_OBJECT, "jsonb");
        this.registerHibernateType(Types.OTHER, "jsonb");
    }
}
