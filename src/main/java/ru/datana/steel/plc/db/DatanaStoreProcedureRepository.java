package ru.datana.steel.plc.db;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;
import ru.datana.steel.plc.config.AppConst;
import ru.datana.steel.plc.model.json.meta.JsonHello;
import ru.datana.steel.plc.model.json.request.JsonRootSensorRequest;

@Repository
@Profile(AppConst.DB_DEV_POSTGRES_PROFILE)
public interface DatanaStoreProcedureRepository extends org.springframework.data.repository.Repository<SimpleEntity, Integer> {

    // @Transactional
    @Procedure(value = "datana.datalake.plc_get_config")
    JsonRootSensorRequest procedureGet(JsonHello fromJson);
}
