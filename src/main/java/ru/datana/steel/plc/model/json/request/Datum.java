package ru.datana.steel.plc.model.json.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

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
    protected String dataBlock;
    /**
     * (Required)
     */
    @JsonProperty("data-vals")
    @Valid
    @NotNull
    protected List<DataVal> dataVals = null;

}