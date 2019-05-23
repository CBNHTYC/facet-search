package ru.kubsu.fs.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kubsu.fs.entity.ElastModel;

import java.util.List;

@Data
@NoArgsConstructor
public class PhoneInfo {
    ElastModel phone;
    List<ElastModel> accessories;
}
