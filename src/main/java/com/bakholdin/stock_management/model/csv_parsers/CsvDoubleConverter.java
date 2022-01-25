package com.bakholdin.stock_management.model.csv_parsers;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor
public class CsvDoubleConverter extends AbstractBeanField<String, Double> {

    @Override
    protected Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        if(Objects.equals(s, "NA"))
            return null;
        return Double.parseDouble(s.replace(",", ""));
    }
}
