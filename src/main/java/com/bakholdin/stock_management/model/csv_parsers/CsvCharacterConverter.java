package com.bakholdin.stock_management.model.csv_parsers;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

import java.util.Objects;

public class CsvCharacterConverter extends AbstractBeanField<String, Character> {
    @Override
    protected Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        if(Objects.equals(s, "NA") || s.isBlank())
            return null;
        return s.charAt(0);
    }
}
