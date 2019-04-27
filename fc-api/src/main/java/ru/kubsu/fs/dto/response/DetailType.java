package ru.kubsu.fs.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DetailType {
    @JsonProperty
    private String ram;
    @JsonProperty
    private String diagonal;
    @JsonProperty
    private String accumulator;
    @JsonProperty
    private String sim;
}
