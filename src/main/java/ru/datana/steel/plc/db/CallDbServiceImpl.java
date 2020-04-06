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
    EntityManager entityManager;

    @Value("${datana.database-options.postgresql-get-function}")
    @Setter
    private String pgNativeSQL;

    private Query funcGet;

    @PostConstruct
    private void init() {
        log.debug("[SQL] pgNativeSQL = " + pgNativeSQL);
        funcGet = entityManager.createNativeQuery(pgNativeSQL);
    }


    @Override
    public void dbLoad() throws SQLException {
        Object dl = entityManager.getDelegate();
        log.debug("[SQL] pgNativeSQL = " + pgNativeSQL);

        Query q = entityManager.createNativeQuery(pgNativeSQL);
        List result = q.getResultList();
        String toJson = result.get(0).toString();
        log.info("toJson: " + toJson);
    }
}
