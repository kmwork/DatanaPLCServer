package ru.datana.steel.plc.model.json.response;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
@Data
public class Response {

    /**
     * (Required)
     */
    @JsonProperty("id")
    @NotNull
    public Integer id;
    /**
     * (Required)
     */
    @JsonProperty("data")
    @NotNull
    public String data;
    /**
     * (Required)
     */
    @JsonProperty("controller-datetime")
    @NotNull
    public String controllerDatetime;
    /**
     * (Required)
     */
    @JsonProperty("status")
    @NotNull
    public Integer status;
    /**
     * (Required)
     */
    @JsonProperty("errors")
    @Valid
    @NotNull
    public List<Error> errors = null;
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