package com.bakholdin.stock_management.config;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class YahooProperties {
    // all instances of {symbol} will be replaced
    @NonNull
    private String tickerQuoteUrlFormat;
    private double requestsPerSecond;
}