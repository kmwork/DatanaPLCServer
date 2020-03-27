package ru.datana.steel.plc.model.json.request;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public String requestId;
    /**
     * (Required)
     */
    @JsonProperty("task_id")
    @NotNull
    public Integer taskId;
    /**
     * (Required)
     */
    @JsonProperty("request-datetime")
    @NotNull
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    public LocalDate requestDatetime;
    /**
     * (Required)
     */
    @JsonProperty("request")
    @Valid
    @NotNull
    public List<Request> request = null;
    @JsonIgnore
    @Valid
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}