package com.example.shopmanagement007;
import javafx.util.StringConverter;

public class SafeIntegerStringConverter extends StringConverter<Integer> {
    @Override
    public Integer fromString(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null; // Return null or a default value
        }
    }

    @Override
    public String toString(Integer value) {
        return value == null ? "" : value.toString();
    }
}
