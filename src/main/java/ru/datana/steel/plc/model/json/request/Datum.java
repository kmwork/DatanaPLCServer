package ru.datana.steel.plc.model.json.request;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "data-block",
        "data-vals"
})
@Data
public class Datum {

    /**
     * (Required)
     */
    @JsonProperty("data-block")
    @NotNull
    public String dataBlock;
    /**
     * (Required)
     */
    @JsonProperty("data-vals")
    @Valid
    @NotNull
    public List<DataVal> dataVals = null;
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