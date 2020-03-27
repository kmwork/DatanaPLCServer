package ru.datana.steel.plc.model.json.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({

})
@Data
public class Error {
    @NotNull
    @JsonProperty("type-code")
    private final Integer typeCode;

    @NotNull
    @JsonProperty("str-args")
    private final String strArgs;

    @NotNull
    @JsonProperty("msg")
    private final String msg;
}
