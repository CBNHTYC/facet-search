package ru.kubsu.fs.dto.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DetailsDto {
    @JsonProperty
    private List<DetailDto> detailDtoList;
}
