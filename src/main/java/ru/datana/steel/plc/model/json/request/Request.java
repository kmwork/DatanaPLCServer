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
        "controller_id",
        "data"
})
@Data
public class Request {

    /**
     * (Required)
     */
    @JsonProperty("controller_id")
    @NotNull
    public Integer controllerId;
    /**
     * (Required)
     */
    @JsonProperty("data")
    @Valid
    @NotNull
    public List<Datum> data = null;
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
