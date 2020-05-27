package ru.datana.steel.plc.model.json.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "data",
        "controller-datetime",
        "status",
        "errors"
})
@Data
public class JsonSensorResponse {

    /**
     * (Required)
     */
    @JsonProperty("id")
    @NotNull
    private long id;
    /**
     * (Required)
     */
    @JsonProperty("data")
    @NotNull
    private BigDecimal data;
    /**
     * (Required)
     */
    @JsonProperty("controller-datetime")
    @NotNull
    private LocalDateTime controllerDatetime;
    /**
     * (Required)
     */
    @JsonProperty("status")
    @NotNull
    private Integer status;


}