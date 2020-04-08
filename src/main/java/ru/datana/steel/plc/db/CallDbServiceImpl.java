package ru.datana.steel.plc.db;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.datana.steel.plc.config.AppConst;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.SQLException;
import java.util.List;


@Service
@Slf4j
@Profile(AppConst.DB_DEV_POSTGRES_PROFILE)
public class CallDbServiceImpl implements CallDbService {

    @PersistenceContext
    protected EntityManager entityManager;

    /**
     * Хранимка генерации запросов на сервер
     */
    @Value("${datana.database-options.postgresql-get-function}")
    @Setter
    private String pgNativeGetSQL;


    /**
     * Хранимка на сохранение данных в базе данных PostgreSQL
     */
    @Value("${datana.database-options.postgresql-save-function}")
    @Setter
    private String pgNativeSaveSQL;

    @PostConstruct
    private void init() {
        log.trace("[SQL: Get] pgNativeGetSQL = " + pgNativeGetSQL);
        log.trace("[SQL: Save] pgNativeSaveSQL = " + pgNativeSaveSQL);
    }


    @Override
    public String dbGet() throws SQLException {
        log.trace("[SQL:Get] старт");
        Query funcGet = entityManager.createNativeQuery(pgNativeGetSQL);
        List result = funcGet.getResultList();
        String toJson = result.get(0).toString();
        log.trace("[SQL:Get] результат = " + toJson);
        return toJson;
    }

    @Override
    public String dbSave(String fromJson) throws SQLException {
        log.trace("[SQL:Save] data = " + fromJson);
        Query funcSave = entityManager.createNativeQuery(pgNativeSaveSQL);
        funcSave.setParameter("fromJson", fromJson);
        String toJson = funcSave.getResultList().get(0).toString();
        log.trace("[SQL:Save] результат = " + toJson);
        return toJson;
    }
}
