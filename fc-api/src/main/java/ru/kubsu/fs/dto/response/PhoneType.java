package ru.kubsu.fs.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PhoneType {
    @JsonProperty
    protected ModelType model;
    @JsonProperty
    protected DetailType details;
    @JsonProperty
    protected ImageListType images;
}
