
package ru.datana.steel.plc.model.json.request;

import com.fasterxml.jackson.annotation.*;

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
public class Example {

    @JsonProperty("request_id")
    private Integer requestId;
    @JsonProperty("task_id")
    private Integer taskId;
    @JsonProperty("request-datetime")
    private String requestDatetime;
    @JsonProperty("request")
    private List<Request> request = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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

    @JsonProperty("request-datetime")
    public String getRequestDatetime() {
        return requestDatetime;
    }

    @JsonProperty("request-datetime")
    public void setRequestDatetime(String requestDatetime) {
        this.requestDatetime = requestDatetime;
    }

    @JsonProperty("request")
    public List<Request> getRequest() {
        return request;
    }

    @JsonProperty("request")
    public void setRequest(List<Request> request) {
        this.request = request;
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
