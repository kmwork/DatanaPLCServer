
package ru.datana.steel.plc.model.json.request;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "data-block",
        "data-vals"
})
public class Datum {

    @JsonProperty("data-block")
    private String dataBlock;
    @JsonProperty("data-vals")
    private List<DataVal> dataVals = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("data-block")
    public String getDataBlock() {
        return dataBlock;
    }

    @JsonProperty("data-block")
    public void setDataBlock(String dataBlock) {
        this.dataBlock = dataBlock;
    }

    @JsonProperty("data-vals")
    public List<DataVal> getDataVals() {
        return dataVals;
    }

    @JsonProperty("data-vals")
    public void setDataVals(List<DataVal> dataVals) {
        this.dataVals = dataVals;
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
