package com.bakholdin.stock_management.model;

import com.univocity.parsers.annotations.Parsed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.util.Objects;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "yahoo")
public class YahooRow {
    @EmbeddedId
    private StockManagementRowId stockManagementRowId;
    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private PerformanceOutlook shortTerm;
    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private PerformanceOutlook midTerm;
    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private PerformanceOutlook longTerm;
    @Column
    private Double estimatedReturn;
    @Parsed
    @Column(length = 50)
    private String fairValue;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        YahooRow yahooRow = (YahooRow) o;
        return stockManagementRowId != null && Objects.equals(stockManagementRowId, yahooRow.stockManagementRowId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stockManagementRowId);
    }
}