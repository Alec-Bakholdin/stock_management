package com.bakholdin.stock_management.config;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class TipRanksProperties {
    @NonNull private String stockDataUrl;
    @NonNull private String stockDataTickersQueryParam;
    @NonNull private String newsSentimentUrl;
    @NonNull private String newsSentimentTickersQueryParam;
}
