package ru.datana.steel.plc.model.json.request;

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
public class JsonDataVal {

    /**
     * (Required)
     */
    @JsonProperty("operation")
    @NotNull
    protected String operation;
    /**
     * (Required)
     */
    @JsonProperty("data-type")
    @NotNull
    protected String dataType;
    /**
     * (Required)
     */
    @JsonProperty("offset")
    @NotNull
    protected Integer offset;
    /**
     * (Required)
     */
    @JsonProperty("bitmask")
    @NotNull
    protected String bitmask;
    /**
     * (Required)
     */
    @JsonProperty("id")
    @NotNull
    protected Integer id;

}