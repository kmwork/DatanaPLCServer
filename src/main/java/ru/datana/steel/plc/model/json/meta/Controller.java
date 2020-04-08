package ru.datana.steel.plc.model.json.meta;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "controller_name",
        "rack",
        "slot",
        "ip",
        "port",
        "write_enable",
        "permanent_connection",
        "timeout"
})
@Data
public class Controller {

    @JsonProperty("id")
    @NotNull
    private Integer id;

    @JsonProperty("controller_name")
    @NotNull
    private String controllerName;

    @JsonProperty("rack")
    @NotNull
    private Integer rack;

    @JsonProperty("slot")
    @NotNull
    private Integer slot;

    @JsonProperty("ip")
    @NotNull
    private String ip;

    @JsonProperty("port")
    private Integer port;

    @JsonProperty("write_enable")
    @NotNull
    private Boolean writeEnable;

    @JsonProperty("permanent_connection")
    @NotNull
    private Boolean permanentConnection;

    @JsonProperty("timeout")
    @NotNull
    private Integer timeout;

}