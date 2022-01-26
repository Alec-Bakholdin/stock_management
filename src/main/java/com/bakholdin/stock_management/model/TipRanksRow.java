package com.bakholdin.stock_management.model;

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
import javax.persistence.Table;
import java.util.Objects;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "tip_ranks")
public class TipRanksRow {
    @EmbeddedId
    private StockManagementRowId stockManagementRowId;
    @Column
    private Double newsSentiment;
    @Column(length = 20)
    private String analystConsensus;
    @Column
    private Double analystPriceTarget;
    @Column(length = 20)
    private String bestAnalystConsensus;
    @Column
    private Double bestAnalystPriceTarget;
    @Column
    private Double estimatedDividendYield;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TipRanksRow tipRanksRow = (TipRanksRow) o;
        return stockManagementRowId != null && Objects.equals(stockManagementRowId, tipRanksRow.stockManagementRowId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stockManagementRowId);
    }
}