package com.bakholdin.stock_management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "tip_ranks")
public class TipRanksRow {
    @EmbeddedId
    private StockManagementRowId id;
    private Double newsSentiment;
    @Column(length = 20)
    private String analystConsensus;
    private Double analystPriceTarget;
    @Column(length = 20)
    private String bestAnalystConsensus;
    private Double bestAnalystPriceTarget;
    private Double estimatedDividendYield;
}