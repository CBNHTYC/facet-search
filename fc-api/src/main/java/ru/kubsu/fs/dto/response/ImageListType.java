package ru.kubsu.fs.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ImageListType {
    @JsonProperty
    private List<String> imageLocationList;
}
