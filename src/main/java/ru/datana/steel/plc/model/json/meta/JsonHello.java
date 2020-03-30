package ru.datana.steel.plc.model.json.meta;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import javax.validation.Valid;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "action",
        "params"
})
@Data
public class JsonHello {

    @JsonProperty("action")
    public String action = "get_plc_proxy_config";
    @JsonProperty("params")
    @Valid
    public JsonHelloParams params;

}