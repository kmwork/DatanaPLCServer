//package ru.datana.steel.plc.db;
//
//import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
//import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;
//import com.vladmihalcea.hibernate.type.json.JsonNodeStringType;
//import com.vladmihalcea.hibernate.type.json.JsonStringType;
//import org.hibernate.annotations.TypeDef;
//import org.hibernate.annotations.TypeDefs;
//import ru.datana.steel.plc.model.json.meta.JsonHello;
//import ru.datana.steel.plc.model.json.request.JsonRootSensorRequest;
//
//import javax.persistence.*;
//
//@Entity
//@TypeDefs({
//        @TypeDef(name = "json", typeClass = JsonStringType.class),
//        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class),
//        @TypeDef(name = "json-node", typeClass = JsonNodeStringType.class),
//        @TypeDef(name = "jsonb-node", typeClass = JsonNodeBinaryType.class),
//})
//@NamedStoredProcedureQueries({
//        @NamedStoredProcedureQuery(name = "get",
//                procedureName = "datana.datalake.plc_get_config", parameters = {
//                @StoredProcedureParameter(mode = ParameterMode.IN, type = JsonHello.class),
//                @StoredProcedureParameter(mode = ParameterMode.OUT, type = JsonRootSensorRequest.class)}) //
//})
//
//public class SimpleEntity {
//    @Id
//    private String id;
//}
