package ru.kubsu.fs.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElastDetailValue {
    @JsonProperty
    String value;
    @JsonProperty
    String unit;
}
