package com.bakholdin.stock_management.model;

import com.bakholdin.stock_management.model.csv_annotations.CsvZacksColumn;
import com.bakholdin.stock_management.model.csv_annotations.CsvZacksDouble;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "companies")
public class CompanyRow implements Serializable {
    @Id
    @Column(name = "symbol", length = 10, nullable = false)
    @CsvZacksColumn(field = "Symbol")
    private String symbol;

    @Column(name = "company_name", length = 512)
    @CsvZacksColumn(field = "Company")
    private String companyName;

    @Column(name = "latest_price")
    @CsvZacksDouble(field = "Price")
    private Double latestPrice;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CompanyRow that = (CompanyRow) o;
        return symbol != null && Objects.equals(symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
