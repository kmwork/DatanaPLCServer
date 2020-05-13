package ru.datana.steel.camel.model.json.meta;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "datablock_write_enable",
        "controllers"
})
@Data
public class JsonMetaRootController {

    @JsonProperty("datablock_write_enable")
    private Boolean datablockWriteEnable;
    @JsonProperty("controllers")
    @Valid
    @NotNull
    private List<Controller> controllers = null;

}