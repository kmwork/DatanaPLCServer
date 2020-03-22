
package ru.datana.steel.plc.model.json.response;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "data",
        "controller-datetime",
        "status",
        "errors"
})
public class Response {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("data")
    private Double data;
    @JsonProperty("controller-datetime")
    private String controllerDatetime;
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("errors")
    private List<Error> errors = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("data")
    public Double getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(Double data) {
        this.data = data;
    }

    @JsonProperty("controller-datetime")
    public String getControllerDatetime() {
        return controllerDatetime;
    }

    @JsonProperty("controller-datetime")
    public void setControllerDatetime(String controllerDatetime) {
        this.controllerDatetime = controllerDatetime;
    }

    @JsonProperty("status")
    public Integer getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Integer status) {
        this.status = status;
    }

    @JsonProperty("errors")
    public List<Error> getErrors() {
        return errors;
    }

    @JsonProperty("errors")
    public void setErrors(List<Error> errors) {
        this.errors = errors;
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
