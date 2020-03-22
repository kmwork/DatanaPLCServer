
package ru.datana.steel.plc.model.json.request;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "operation",
        "data-type",
        "offset",
        "bitmask",
        "id"
})
public class DataVal {

    @JsonProperty("operation")
    private String operation;
    @JsonProperty("data-type")
    private String dataType;
    @JsonProperty("offset")
    private Integer offset;
    @JsonProperty("bitmask")
    private String bitmask;
    @JsonProperty("id")
    private Integer id;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("operation")
    public String getOperation() {
        return operation;
    }

    @JsonProperty("operation")
    public void setOperation(String operation) {
        this.operation = operation;
    }

    @JsonProperty("data-type")
    public String getDataType() {
        return dataType;
    }

    @JsonProperty("data-type")
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    @JsonProperty("offset")
    public Integer getOffset() {
        return offset;
    }

    @JsonProperty("offset")
    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    @JsonProperty("bitmask")
    public String getBitmask() {
        return bitmask;
    }

    @JsonProperty("bitmask")
    public void setBitmask(String bitmask) {
        this.bitmask = bitmask;
    }

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
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
