package ru.kubsu.fs.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {
    @JsonProperty
    private String userId;
    @JsonProperty
    private String email;
}
