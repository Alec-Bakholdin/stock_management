package com.bakholdin.stock_management.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TipRanksStockDataDto {
    private String ticker;
    private Integer newsSentiment;
    private TipRankConsensusDto analystConsensus;
    private TipRankConsensusDto bestAnalystConsensus;

    @JsonProperty("priceTarget")
    private Double analystPriceTarget;
    @JsonProperty("bestPriceTarget")
    private Double bestAnalystPriceTarget;
    @JsonProperty("dividendYield")
    private Double estimatedDividendYield;
}
