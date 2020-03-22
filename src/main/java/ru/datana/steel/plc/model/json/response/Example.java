
package ru.datana.steel.plc.model.json.response;

import com.fasterxml.jackson.annotation.*;

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
public class Example {

    @JsonProperty("request-datetime")
    private String requestDatetime;
    @JsonProperty("request-datetime-proxy")
    private String requestDatetimeProxy;
    @JsonProperty("response-datetime")
    private String responseDatetime;
    @JsonProperty("request_id")
    private Integer requestId;
    @JsonProperty("task_id")
    private Integer taskId;
    @JsonProperty("response")
    private List<Response> response = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("request-datetime")
    public String getRequestDatetime() {
        return requestDatetime;
    }

    @JsonProperty("request-datetime")
    public void setRequestDatetime(String requestDatetime) {
        this.requestDatetime = requestDatetime;
    }

    @JsonProperty("request-datetime-proxy")
    public String getRequestDatetimeProxy() {
        return requestDatetimeProxy;
    }

    @JsonProperty("request-datetime-proxy")
    public void setRequestDatetimeProxy(String requestDatetimeProxy) {
        this.requestDatetimeProxy = requestDatetimeProxy;
    }

    @JsonProperty("response-datetime")
    public String getResponseDatetime() {
        return responseDatetime;
    }

    @JsonProperty("response-datetime")
    public void setResponseDatetime(String responseDatetime) {
        this.responseDatetime = responseDatetime;
    }

    @JsonProperty("request_id")
    public Integer getRequestId() {
        return requestId;
    }

    @JsonProperty("request_id")
    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    @JsonProperty("task_id")
    public Integer getTaskId() {
        return taskId;
    }

    @JsonProperty("task_id")
    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    @JsonProperty("response")
    public List<Response> getResponse() {
        return response;
    }

    @JsonProperty("response")
    public void setResponse(List<Response> response) {
        this.response = response;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
