package com.bakholdin.stock_management.service.TipRanks.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsSentiment{
    private String ticker;
    @JsonProperty("score")
    private Double newsSentiment;
}
