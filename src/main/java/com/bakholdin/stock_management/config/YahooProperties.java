package com.bakholdin.stock_management.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YahooProperties {
    // all instances of {symbol} will be replaced
    private String tickerQuoteUrlFormat;
}
