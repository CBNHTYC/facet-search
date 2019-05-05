package ru.kubsu.fs.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ModelType {
    @JsonProperty
    private String modelId;
    @JsonProperty
    private String vendor;
    @JsonProperty
    private String modelName;
    @JsonProperty
    private String type;
    @JsonProperty
    private String description;
    @JsonProperty
    private int price;
    @JsonProperty
    private int views;
}
