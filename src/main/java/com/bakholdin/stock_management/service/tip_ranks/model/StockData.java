package com.bakholdin.stock_management.service.tip_ranks.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockData{
    private String ticker;

    private AnalystConsensus analystConsensus;
    private AnalystConsensus bestAnalystConsensus;

    @JsonProperty("priceTarget")
    private Double analystPriceTarget;
    @JsonProperty("bestPriceTarget")
    private Double bestAnalystPriceTarget;

    @JsonProperty("dividendYield")
    private Double estimatedDividendYield;
}
