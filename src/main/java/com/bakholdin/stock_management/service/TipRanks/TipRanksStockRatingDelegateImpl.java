package com.bakholdin.stock_management.service.TipRanks;

import com.bakholdin.stock_management.model.TipRanksRow;
import com.bakholdin.stock_management.service.StockRatingDelegate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TipRanksStockRatingDelegateImpl implements StockRatingDelegate<TipRanksRow> {

    @Override
    public List<TipRanksRow> fetchRows() {
        return null;
    }

    @Override
    public void saveRows(Collection<TipRanksRow> rows) {

    }
}
