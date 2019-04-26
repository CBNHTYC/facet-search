package ru.kubsu.fs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ElastModel {
    private String modelId;
    private String vendor;
    private String modelName;
    private String diagonal;
    private String ram;
    private String accumulator;
    private String sim;
    private String description;
    private Integer price;
    private Integer views;
}
