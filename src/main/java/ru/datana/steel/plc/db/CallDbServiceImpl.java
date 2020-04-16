package ru.datana.steel.plc.db;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.datana.steel.plc.config.AppConst;
import ru.datana.steel.plc.config.RestSpringConfig;
import ru.datana.steel.plc.util.AppException;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@Service
@Slf4j
@Profile(AppConst.DB_DEV_POSTGRES_PROFILE)
public class CallDbServiceImpl implements CallDbService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private RestSpringConfig restSpringConfig;

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
        if (log.isDebugEnabled()) {
            log.debug("[SQL:Init:Get] pgNativeGetSQL = " + pgNativeGetSQL);
            log.debug("[SQL:Init:Save] pgNativeSaveSQL = " + pgNativeSaveSQL);
        }
    }


    @Override
    public String dbGet() {
        log.info("[SQL:Get] старт");
        Query funcGet = entityManager.createNativeQuery(pgNativeGetSQL);
        List result = funcGet.getResultList();
        String toJson = result.get(0).toString();
        if (log.isTraceEnabled())
            log.trace("[SQL:Get] результат = " + toJson);
        return toJson;
    }

    @Override
    public String dbSave(String fromJson, int threadCountMax, int threadCurrent) {
        log.info("[SQL:Save] start");
        if (log.isTraceEnabled())
            log.trace("[SQL:Save] data = " + fromJson);
        Query funcSave = entityManager.createNativeQuery(pgNativeSaveSQL);
        funcSave.setParameter("fromJson", fromJson);
        funcSave.setParameter("threadCountMax", threadCountMax);
        funcSave.setParameter("threadCurrent", threadCurrent);
        String toJson = funcSave.getResultList().get(0).toString();
        if (log.isTraceEnabled())
            log.trace("[SQL:Save] результат = " + toJson);
        log.info("[SQL:Save] конец");
        return toJson;
    }


    @Override
    @Async
    @Transactional
    public void saveAsync(String prefixLog, String resultFromJson, int threadIndex, int threadCountMax, AtomicInteger threadCount) throws AppException, InterruptedException {
        try {
            int threadNumber = threadIndex + 1;
            prefixLog += "[Поток: " + threadNumber + "] ";
            log.debug(prefixLog + "save db");
            String saveJson = dbSave(resultFromJson, threadCountMax, threadNumber);
            restSpringConfig.formatBeautyJson(prefixLog + " [Save:RESULT] ", saveJson);
        } finally {
            threadCount.decrementAndGet();
        }

    }
}
