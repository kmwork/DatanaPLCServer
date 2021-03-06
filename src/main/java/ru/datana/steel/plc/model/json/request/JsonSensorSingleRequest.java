package ru.datana.steel.plc.model.json.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "controller_id",
        "data"
})
@Data
public class JsonSensorSingleRequest {

    /**
     * (Required)
     */
    @JsonProperty("controller_id")
    @NotNull
    private Integer controllerId;
    /**
     * (Required)
     */
    @JsonProperty("data")
    @Valid
    @NotNull
    private List<JsonSensorDatum> data = null;

}
