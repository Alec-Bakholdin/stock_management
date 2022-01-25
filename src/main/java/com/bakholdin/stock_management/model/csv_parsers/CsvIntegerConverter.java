package com.bakholdin.stock_management.model.csv_parsers;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

import java.util.Objects;

public class CsvIntegerConverter extends AbstractBeanField<String, Integer> {
    @Override
    protected Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        if(Objects.equals(s, "NA"))
            return null;
        return Integer.parseInt(s.replace(",", ""));
    }
}
