package ru.datana.steel.plc.model.json.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "data",
        "controller-datetime",
        "status",
        "errors"
})
@Data
public class JsonResponse {

    /**
     * (Required)
     */
    @JsonProperty("id")
    @NotNull
    protected String id;
    /**
     * (Required)
     */
    @JsonProperty("data")
    @NotNull
    protected String data;
    /**
     * (Required)
     */
    @JsonProperty("controller-datetime")
    @NotNull
    protected LocalDateTime controllerDatetime;
    /**
     * (Required)
     */
    @JsonProperty("status")
    @NotNull
    protected Integer status;
    /**
     * (Required)
     */
    @JsonProperty("errors")
    @Valid
    @NotNull
    protected List<JsonError> errors = null;

}