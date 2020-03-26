package ru.datana.steel.plc.model.json.request;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
@Data
public class DataVal {

    /**
     * (Required)
     */
    @JsonProperty("operation")
    @NotNull
    public String operation;
    /**
     * (Required)
     */
    @JsonProperty("data-type")
    @NotNull
    public String dataType;
    /**
     * (Required)
     */
    @JsonProperty("offset")
    @NotNull
    public Integer offset;
    /**
     * (Required)
     */
    @JsonProperty("bitmask")
    @NotNull
    public String bitmask;
    /**
     * (Required)
     */
    @JsonProperty("id")
    @NotNull
    public Integer id;
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