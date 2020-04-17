package ru.datana.steel.plc.db;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.jpa.TypedParameterValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.datana.steel.plc.config.AppConst;
import ru.datana.steel.plc.config.RestSpringConfig;
import ru.datana.steel.plc.model.json.request.JsonRootSensorRequest;
import ru.datana.steel.plc.util.AppException;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;


@Repository
@Slf4j
@Profile(AppConst.DB_DEV_POSTGRES_PROFILE)
public class CallDbServiceImpl implements CallDbService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private RestSpringConfig restSpringConfig;

//    /**
//     * Хранимка генерации запросов на сервер
//     */
//    @Value("${datana.database-options.postgresql-get-function}")
//    @Setter
//    private String pgNativeGetSQL;


    /**
     * Хранимка на сохранение данных в базе данных PostgreSQL
     */
    @Value("${datana.database-options.postgresql-save-function}")
    @Setter
    private String pgNativeSaveSQL;

    @Autowired
    private DatanaStoreProcedureRepository procedureRepository;

    @PostConstruct
    private void init() {
        if (log.isDebugEnabled()) {
            //log.debug("[SQL:Init:Get] pgNativeGetSQL = " + pgNativeGetSQL);
            log.debug("[SQL:Init:Save] pgNativeSaveSQL = " + pgNativeSaveSQL);
        }

//
//22        queryGet = entityManager.createStoredProcedureQuery(pgNativeGetSQL);
//        queryGet.registerStoredProcedureParameter(0, Object.class, ParameterMode.OUT);
//        queryGet.registerStoredProcedureParameter(1, Object.class, ParameterMode.IN);
    }


    @Override
    public JsonRootSensorRequest dbGet() {
        log.info("[SQL:Get] старт");
        String strValueGet = "{\"action\": \"plc_get_proxy_client_config\",\"params\": {\"task_id\": 1}}";
        //JsonBinaryType pgInputValue = new JsonBinaryType(strValueGet);
        Object pgResultObject = procedureRepository.procedureGet(strValueGet);
        //queryGet.setParameter(1, strValueGet);
        //Query funcGet = entityManager.createStoredProcedureQuery(pgNativeGetSQL, String.class);
        log.debug("[SQL:Get] результат = " + pgResultObject);
        JsonRootSensorRequest pgResult = null;// (JsonRootSensorRequest) funcGet.getSingleResult();
        if (log.isTraceEnabled())
            log.trace("[SQL:Get] результат = " + pgResult);
        return pgResult;
    }

    @Override
    public String dbSave(JsonRootSensorRequest fromJson, int threadCountMax, int threadCurrent) throws SQLException {
        log.info("[SQL:Save] start");
        if (log.isTraceEnabled())
            log.trace("[SQL:Save] data = " + fromJson);
        Query funcSave = entityManager.createNativeQuery(pgNativeSaveSQL, JsonBinaryType.INSTANCE.getClass());
        TypedParameterValue pgValue = new TypedParameterValue(JsonBinaryType.INSTANCE, fromJson);
        funcSave.setParameter("fromJson", pgValue);
        funcSave.setParameter("threadCountMax", threadCountMax);
        funcSave.setParameter("threadCurrent", threadCurrent);
        Object pgResult = funcSave.getSingleResult();
        log.debug("[SQL:Save] pgResult.class =" + pgResult.getClass() + "");
        String toJson = pgResult.toString();
        if (log.isTraceEnabled())
            log.trace("[SQL:Save] результат = " + toJson);
        log.info("[SQL:Save] конец");
        return toJson;
    }


    @Override
    @Async
    @Transactional
    public void saveAsync(String prefixLog, JsonRootSensorRequest resultFromJson, int threadIndex, int threadCountMax, AtomicInteger threadCount) throws AppException, InterruptedException {
        try {
            int threadNumber = threadIndex + 1;
            prefixLog += "[Поток: " + threadNumber + "] ";
            log.debug(prefixLog + "save db");
            String saveJson = dbSave(resultFromJson, threadCountMax, threadNumber);
            restSpringConfig.formatBeautyJson(prefixLog + " [Save:RESULT] ", saveJson);
        } catch (SQLException e) {
            log.error(AppConst.ERROR_LOG_PREFIX + "Ошибка при вызове хранимки save");
        } finally {
            threadCount.decrementAndGet();
        }

    }
}
