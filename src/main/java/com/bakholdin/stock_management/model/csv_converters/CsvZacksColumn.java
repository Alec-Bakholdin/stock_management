package com.bakholdin.stock_management.model.csv_converters;

import com.univocity.parsers.annotations.Copy;
import com.univocity.parsers.annotations.NullString;
import com.univocity.parsers.annotations.Parsed;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})

@Parsed
@NullString(nulls = "NA")
public @interface CsvZacksColumn {
    @Copy(to = Parsed.class)
    String field() default "";
}
