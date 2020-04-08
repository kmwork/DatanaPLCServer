package ru.datana.steel.plc.model.json.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "request-datetime",
        "request-datetime-proxy",
        "response-datetime",
        "request_id",
        "task_id",
        "response"
})
@Data
public class JsonRootSensorResponse {

    /**
     * (Required)
     */
    @JsonProperty("request-datetime")
    @NotNull
    private LocalDateTime requestDatetime;
    /**
     * (Required)
     */
    @JsonProperty("request-datetime-proxy")
    @NotNull
    private LocalDateTime requestDatetimeProxy;
    /**
     * (Required)
     */
    @JsonProperty("response-datetime")
    @NotNull
    private LocalDateTime responseDatetime;
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
    @JsonProperty("response")
    @Valid
    @NotNull
    private List<JsonSensorResponse> response = null;

}