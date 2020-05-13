package ru.datana.steel.camel.model.json.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({

})
@Data
public class JsonSensorError {
    @NotNull
    @JsonProperty("type-code")
    private Integer typeCode;

    @NotNull
    @JsonProperty("str-args")
    private String strArgs;

    @NotNull
    @JsonProperty("msg")
    private String msg;
}
