//package ru.datana.steel.plc.db;
//
//import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
//import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;
//import com.vladmihalcea.hibernate.type.json.JsonNodeStringType;
//import com.vladmihalcea.hibernate.type.json.JsonStringType;
//import org.hibernate.annotations.ParamDef;
//import org.hibernate.annotations.TypeDef;
//import org.hibernate.annotations.TypeDefs;
//import org.springframework.context.annotation.Profile;
//import org.springframework.data.jpa.repository.query.Procedure;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//import ru.datana.steel.plc.config.AppConst;
//import ru.datana.steel.plc.model.json.meta.JsonHello;
//import ru.datana.steel.plc.model.json.request.JsonRootSensorRequest;
//
//import javax.persistence.NamedStoredProcedureQueries;
//import javax.persistence.NamedStoredProcedureQuery;
//import javax.persistence.ParameterMode;
//import javax.persistence.StoredProcedureParameter;
//
//@Repository
//@Profile(AppConst.DB_DEV_POSTGRES_PROFILE)
//@TypeDefs({
//        @TypeDef(name = "json", typeClass = JsonStringType.class),
//        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class),
//        @TypeDef(name = "json-node", typeClass = JsonNodeStringType.class),
//        @TypeDef(name = "jsonb-node", typeClass = JsonNodeBinaryType.class),
//})
//public interface DatanaStoreProcedureRepository extends org.springframework.data.repository.Repository<SimpleEntity, Integer> {
//
//    // @Transactional
//    @Procedure(value = "get")
//    JsonRootSensorRequest procedureGet(@Param(type="jsonb") JsonHello fromJson);
//}
