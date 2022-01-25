package com.bakholdin.stock_management.model;

import com.bakholdin.stock_management.model.csv_converters.CsvZacksColumn;
import com.bakholdin.stock_management.model.csv_converters.CsvZacksDouble;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "companies")
public class CompanyRow implements Serializable {
    @Id
    @Column(length = 10, nullable = false)
    @CsvZacksColumn(field = "Symbol")
    private String symbol;

    @Column(length = 512)
    @CsvZacksColumn(field = "Company")
    private String companyName;

    @Column
    @CsvZacksDouble(field = "Price")
    private Double latestPrice;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CompanyRow companyRow = (CompanyRow) o;
        return symbol != null && Objects.equals(symbol, companyRow.symbol);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
