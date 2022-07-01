package com.bakholdin.stock_management;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockUpdaterExceptions {
    private Exception zacksException;
    private Exception tipRanksException;
    private Exception yahooException;

    public boolean hasNoExceptions() {
        return zacksException == null && tipRanksException == null && yahooException == null;
    }
}
