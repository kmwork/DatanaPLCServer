package ru.datana.steel.plc.model.json.request;

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
        "request_id",
        "task_id",
        "request-datetime",
        "request",
        "status"
})
@Data
public class JsonRootSensorRequest {

    /**
     * (Required)
     */
    @JsonProperty("request_id")
    @NotNull
    private String requestId;
    /**
     * (Required)
     */
    @JsonProperty("task_id")
    @NotNull
    private Integer taskId;
    /**
     * (Required)
     */
    @JsonProperty("request-datetime")
    @NotNull
    private LocalDateTime requestDatetime;
    /**
     * (Required)
     */
    @JsonProperty("request")
    @Valid
    @NotNull
    private List<JsonSensorSingleRequest> request = null;


    @JsonProperty("status")
    @Null
    private Integer status;


    @JsonProperty("timeout")
    @Null
    private Long timeout;

}