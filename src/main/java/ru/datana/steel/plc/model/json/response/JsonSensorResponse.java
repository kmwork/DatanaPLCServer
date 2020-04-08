package ru.datana.steel.plc.model.json.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;
import java.util.List;

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
    protected Integer id;
    /**
     * (Required)
     */
    @JsonProperty("data")
    @NotNull
    protected String data;
    /**
     * (Required)
     */
    @JsonProperty("controller-datetime")
    @NotNull
    protected LocalDateTime controllerDatetime;
    /**
     * (Required)
     */
    @JsonProperty("status")
    @NotNull
    protected Integer status;
    /**
     * (Required)
     */
    @JsonProperty("errors")
    @Valid
    @NotNull
    protected List<JsonSensorError> errors = null;

    @JsonProperty("s7StartTime")
    @Null
    private LocalDateTime s7StartTime;

    @JsonProperty("s7EndTime")
    @Null
    private LocalDateTime s7EndTime;


    @JsonProperty("deltaNano")
    @Null
    private Long deltaNano;

}