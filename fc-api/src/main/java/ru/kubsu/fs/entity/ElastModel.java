package ru.kubsu.fs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ElastModel {
    private String modelId;
    private String category;
    private String vendor;
    private String modelName;
    private String fullName;
    private String type;
    private String diagonal;
    private String ram;
    private String accumulator;
    private String sim;
    private String powerType;
    private String description;
    private Integer price;
    private Integer views;
    private List<String> imageLocationList;
}

