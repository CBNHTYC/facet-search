package ru.kubsu.fs.dto.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kubsu.fs.entity.ElastDetailValue;

import java.util.List;

@Data
@NoArgsConstructor
public class DetailDto {
    @JsonProperty
    private long detailId;
    @JsonProperty
    private String name;
    @JsonProperty
    private long parentDetailId;
    @JsonProperty
    private List<ElastDetailValue> elastDetailValueList;
}
