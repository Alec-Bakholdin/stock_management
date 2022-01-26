package com.bakholdin.stock_management.service.TipRanks.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockData {
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
