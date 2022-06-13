package com.bakholdin.stock_management.config.properties;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class TipRanksProperties {
    @NonNull private String stockDataUrl;
    private Set<String> excludedTickers;
}
