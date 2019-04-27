package ru.kubsu.fs.dto.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ParametrizedQuery {
    @JsonProperty
    private List<SimpleParameter> simpleParameter;
    @JsonProperty
    private List<RangeParameter> rangeParameter;
}
