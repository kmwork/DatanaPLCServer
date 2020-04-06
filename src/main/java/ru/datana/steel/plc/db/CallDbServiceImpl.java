package ru.datana.steel.plc.db;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.SQLException;
import java.util.List;


@Service
@Slf4j
public class CallDbServiceImpl implements CallDbService {

    @PersistenceContext
    protected EntityManager entityManager;

    @Value("${datana.database-options.postgresql-get-function}")
    @Setter
    private String pgNativeGetSQL;


    @Value("${datana.database-options.postgresql-save-function}")
    @Setter
    private String pgNativeSaveSQL;

    private Query funcGet;
    private Query funcSave;

    @PostConstruct
    private void init() {
        log.debug("[SQL: Get] pgNativeGetSQL = " + pgNativeGetSQL);
        log.debug("[SQL: Save] pgNativeSaveSQL = " + pgNativeSaveSQL);
        funcGet = entityManager.createNativeQuery(pgNativeGetSQL);
        funcSave = entityManager.createNativeQuery(pgNativeSaveSQL);
    }


    @Override
    public String dbGet() throws SQLException {
        log.debug("[SQL:Get] старт");

        List result = funcGet.getResultList();
        String toJson = result.get(0).toString();
        log.debug("[SQL:Get] результат = " + toJson);
        return toJson;
    }

    @Override
    public String dbSave(String fromJson) throws SQLException {
        log.debug("[SQL:Save] data = " + fromJson);
        funcSave.setParameter("fromJson", fromJson);
        String toJson = funcGet.getResultList().get(0).toString();
        log.debug("[SQL:Get] результат = " + toJson);
        return toJson;
    }
}
