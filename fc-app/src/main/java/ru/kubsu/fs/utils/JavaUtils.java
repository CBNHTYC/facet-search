package ru.kubsu.fs.utils;

import java.util.Optional;
import java.util.function.Supplier;

public class JavaUtils {

    public static final <T> String get(Supplier<T> supplier) {
        String res;
        try {
            res = (String) supplier.get();
            if (res == null) {
                res = "";
            }
        } catch (Exception ex) {
            res = "";
        }
        return res;
    }

    public static final <T> T getSafely(Supplier<T> supplier, T defaultValue) {
        T res;
        try {
            res = supplier.get();
            if (res == null) {
                res = defaultValue;
            }
        } catch (Exception ex) {
            res = defaultValue;
        }
        return res;
    }

    public static <T> Optional<T> getOptional(Supplier<T> supplier) {
        Optional res = Optional.empty();
        try {
            res = Optional.ofNullable(supplier.get());
        } catch (Exception ex) {

        }
        return res;
    }
}
