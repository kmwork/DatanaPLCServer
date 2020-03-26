package ru.datana.steel.plc.model.json.response;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public LocalDate requestDatetime;
    /**
     * (Required)
     */
    @JsonProperty("request-datetime-proxy")
    @NotNull
    public LocalDate requestDatetimeProxy;
    /**
     * (Required)
     */
    @JsonProperty("response-datetime")
    @NotNull
    public LocalDate responseDatetime;
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
    @JsonProperty("response")
    @Valid
    @NotNull
    public List<Response> response = null;
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