package ru.datana.steel.plc.db;

import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PGobject;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.sql.SQLException;


@Service
@Slf4j
public class CallDbServiceImpl implements CallDbService {
    private static final String jsonReqString = "{\"action\": \"plc_get_proxy_client_config\",\"params\": {\"task_id\": 1}}";
    private static final String IN_PARAM = "inParam";
    private static final String OUT_PARAM = "outParam";

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public void dbLoad() throws SQLException {

        log.info("kostya =============================start");
        PGobject fromJson = new PGobject();
        fromJson.setType("json");
        fromJson.setValue(jsonReqString);

        StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("datalake.plc_get_config");
        storedProcedure.registerStoredProcedureParameter(IN_PARAM, PGobject.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(OUT_PARAM, PGobject.class, ParameterMode.OUT);
        storedProcedure.setParameter(IN_PARAM, fromJson);
        storedProcedure.execute();
        PGobject toJson = (PGobject) storedProcedure.getOutputParameterValue(OUT_PARAM);
        log.info("kostya =============================end");
        log.info("toJson: " + toJson.getValue());
    }
}
