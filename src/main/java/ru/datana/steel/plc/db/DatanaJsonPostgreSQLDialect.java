package ru.datana.steel.plc.db;

import com.github.wenerme.postjava.hibernate.dialect.PostgreSQLJsonDialect;
import lombok.extern.slf4j.Slf4j;

/**
 * Диалект по работе PostgreSQL при JSON типах
 *
 * @author timfulmer
 */
@Slf4j
public class DatanaJsonPostgreSQLDialect extends PostgreSQLJsonDialect {

    public DatanaJsonPostgreSQLDialect() {
        super();
        log.debug("[Datana:PostgreSQL] Включен диалект: " + getClass().getName());
    }
}
