package ru.kubsu.fs.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum DetailsEnum {
    VENDOR(new DetailDictionary("Производитель","vendor")),
    MODEL(new DetailDictionary("Модель", "model")),
    DIAGONAL(new DetailDictionary("Диагональ экрана", "diagonal")),
    RAM(new DetailDictionary("Оперативная память", "ram")),
    ACCUMULATOR(new DetailDictionary("Емкость аккумулятора", "accumulator")),
    SIM(new DetailDictionary("Поддержка двух SIM-карт", "sim"));

    private final DetailDictionary value;

    public static List<String> getRuNames() {
        DetailDictionary[] dictionaries = Arrays.stream(DetailsEnum.values()).map(DetailsEnum::getValue).toArray(DetailDictionary[]::new);
        return Arrays.stream(dictionaries).map(DetailDictionary::getRu).collect(Collectors.toList());
    }

    public static List<String> getEnNames() {
        DetailDictionary[] dictionaries = Arrays.stream(DetailsEnum.values()).map(DetailsEnum::getValue).toArray(DetailDictionary[]::new);
        return Arrays.stream(dictionaries).map(DetailDictionary::getEn).collect(Collectors.toList());
    }
}