package com.bakholdin.stock_management.service;

import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

public interface StockRatingDelegate<T>{
    List<T> fetchRows();
    @Transactional
    void saveRows(Collection<T> rows);
}
