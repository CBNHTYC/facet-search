package ru.kubsu.fs.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum DetailsEnum {
    DIAGONAL("Диагональ экрана"),
    RAM("Оперативная память"),
    ACCUMULATOR("Емкость аккумулятора"),
    SIM("Поддержка двух SIM-карт");

    private final String value;

    public static String[] getNames() {
        return Arrays.stream(DetailsEnum.values()).map(DetailsEnum::getValue).toArray(String[]::new);
    }
}