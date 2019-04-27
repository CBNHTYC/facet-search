package ru.kubsu.fs.dto.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DetailDto {
    @JsonProperty
    private long detailId;
    @JsonProperty
    private String name;
    @JsonProperty
    private long parentDetailId;
}
