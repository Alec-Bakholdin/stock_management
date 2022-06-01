package com.bakholdin.stock_management.config;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class TipRanksProperties {
    @NonNull private String stockDataUrl;
}
