package ru.datana.steel.plc.model.json.request;

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
        "request_id",
        "task_id",
        "request-datetime",
        "request"
})
@Data
public class JsonRootRequest {

    /**
     * (Required)
     */
    @JsonProperty("request_id")
    @NotNull
    protected String requestId;
    /**
     * (Required)
     */
    @JsonProperty("task_id")
    @NotNull
    protected Integer taskId;
    /**
     * (Required)
     */
    @JsonProperty("request-datetime")
    @NotNull
    protected LocalDateTime requestDatetime;
    /**
     * (Required)
     */
    @JsonProperty("request")
    @Valid
    @NotNull
    protected List<JsonRequest> request = null;

}