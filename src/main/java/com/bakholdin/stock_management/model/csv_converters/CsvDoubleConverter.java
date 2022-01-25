package com.bakholdin.stock_management.model.csv_converters;

import com.univocity.parsers.conversions.Conversion;

import java.util.Objects;

public class CsvDoubleConverter implements Conversion<String, Double> {

    @Override
    public Double execute(String s) {
        if(Objects.equals(s, "NA"))
            return null;
        return Double.parseDouble(s.replace(",", ""));
    }

    @Override
    public String revert(Double o) {
        return o == null ? null : o.toString();
    }
}
