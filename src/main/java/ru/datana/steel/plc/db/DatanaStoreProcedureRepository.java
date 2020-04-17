package ru.datana.steel.plc.db;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.datana.steel.plc.config.AppConst;

@Repository
@Profile(AppConst.DB_DEV_POSTGRES_PROFILE)
public interface DatanaStoreProcedureRepository extends org.springframework.data.repository.Repository<SimpleEntity, Integer> {

    @Transactional
    @Procedure(value = "datana.datalake.plc_get_config(:fromJson)")
    Object procedureGet(@Param("fromJson") Object tNameOrSomething);
}
