package ru.kubsu.fs.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ModelType {
    @JsonProperty
    private String id;
    @JsonProperty
    private String vendor;
    @JsonProperty
    private String name;
    @JsonProperty
    private String type;
    @JsonProperty
    private String description;
    @JsonProperty
    private int price;
    @JsonProperty
    private int views;
}
