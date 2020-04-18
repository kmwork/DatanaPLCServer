package ru.datana.steel.plc.db;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import javax.persistence.*;

@Entity
@NamedStoredProcedureQueries({ //
        @NamedStoredProcedureQuery(name = "get",
                procedureName = "datana.datalake.plc_get_config", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, type = JsonBinaryType.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, type = JsonBinaryType.class)}) //
})
public class SimpleEntity {
    @Id
    private String id;
}
