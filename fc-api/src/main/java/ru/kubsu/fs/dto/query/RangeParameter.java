package ru.kubsu.fs.dto.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RangeParameter {
    @JsonProperty
    private String name;
    @JsonProperty
    private String valueBegin;
    @JsonProperty
    private String valueEnd;
}
