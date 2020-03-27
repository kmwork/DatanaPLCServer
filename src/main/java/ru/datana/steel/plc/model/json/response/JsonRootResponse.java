package ru.datana.steel.plc.model.json.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
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
public class JsonRootResponse {

    /**
     * (Required)
     */
    @JsonProperty("request-datetime")
    @NotNull
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    protected LocalDateTime requestDatetime;
    /**
     * (Required)
     */
    @JsonProperty("request-datetime-proxy")
    @NotNull
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    protected LocalDateTime requestDatetimeProxy;
    /**
     * (Required)
     */
    @JsonProperty("response-datetime")
    @NotNull
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    protected LocalDateTime responseDatetime;
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
    @JsonProperty("response")
    @Valid
    @NotNull
    protected List<JsonResponse> response = null;

}