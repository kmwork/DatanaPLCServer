package ru.datana.steel.camel.model.json.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "operation",
        "data-type",
        "offset",
        "bitmask",
        "id"
})
@Data
public class JsonSensorDataVal {

    /**
     * (Required)
     */
    @JsonProperty("operation")
    @NotNull
    private String operation;
    /**
     * (Required)
     */
    @JsonProperty("data-type")
    @NotNull
    private String dataType;
    /**
     * (Required)
     */
    @JsonProperty("offset")
    @NotNull
    private Integer offset;
    /**
     * (Required)
     */
    @JsonProperty("bitmask")
    @NotNull
    private String bitmask;
    /**
     * (Required)
     */
    @JsonProperty("id")
    @NotNull
    private Integer id;

}