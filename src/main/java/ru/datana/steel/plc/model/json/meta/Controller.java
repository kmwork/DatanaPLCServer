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
    protected Integer id;

    @JsonProperty("controller_name")
    @NotNull
    protected String controllerName;

    @JsonProperty("rack")
    @NotNull
    protected Integer rack;

    @JsonProperty("slot")
    @NotNull
    protected Integer slot;

    @JsonProperty("ip")
    @NotNull
    protected String ip;

    @JsonProperty("port")
    protected Integer port;

    @JsonProperty("write_enable")
    @NotNull
    protected Boolean writeEnable;

    @JsonProperty("permanent_connection")
    @NotNull
    protected Boolean permanentConnection;

    @JsonProperty("timeout")
    @NotNull
    protected Integer timeout;

}