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
import java.util.List;


@Service
@Slf4j
@Profile(AppConst.DB_DEV_POSTGRES_PROFILE)
public class CallDbServiceImpl implements CallDbService {

    @PersistenceContext
    private EntityManager entityManager;

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
        log.debug("[SQL:Init:Get] pgNativeGetSQL = " + pgNativeGetSQL);
        log.debug("[SQL:Init:Save] pgNativeSaveSQL = " + pgNativeSaveSQL);
    }


    @Override
    public String dbGet() {
        log.debug("[SQL:Get] старт");
        Query funcGet = entityManager.createNativeQuery(pgNativeGetSQL);
        List result = funcGet.getResultList();
        String toJson = result.get(0).toString();
        log.info("[SQL:Get] результат = " + toJson);
        return toJson;
    }

    @Override
    public String dbSave(String fromJson, int threadCountMax, int threadCurrent) {
        log.debug("[SQL:Save] data = " + fromJson);
        Query funcSave = entityManager.createNativeQuery(pgNativeSaveSQL);
        funcSave.setParameter("fromJson", fromJson);
        funcSave.setParameter("threadCountMax", threadCountMax);
        funcSave.setParameter("threadCurrent", threadCurrent);
        String toJson = funcSave.getResultList().get(0).toString();
        log.info("[SQL:Save] результат = " + toJson);
        return toJson;
    }
}
