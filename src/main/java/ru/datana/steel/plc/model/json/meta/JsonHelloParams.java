package ru.datana.steel.plc.model.json.meta;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "task_id"
})
@Data
public class JsonHelloParams {

    /**
     * (Required)
     */
    @JsonProperty("task_id")
    @NotNull
    private Integer taskId;
}