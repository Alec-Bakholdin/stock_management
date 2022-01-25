package com.bakholdin.stock_management.service;

import java.util.Collection;
import java.util.List;

public interface StockRatingDelegate<T>{
    List<T> fetchRows();
    void saveRows(Collection<T> rows);
}
