package com.bakholdin.stock_management.service.TipRanks.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewsSentiment {
    private String ticker;
    @JsonProperty("score")
    private Double newsSentiment;
}
