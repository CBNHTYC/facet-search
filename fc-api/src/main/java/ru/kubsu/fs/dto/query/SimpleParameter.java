package ru.kubsu.fs.dto.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SimpleParameter {
    @JsonProperty
    private String name;
    @JsonProperty
    private String value;
}
