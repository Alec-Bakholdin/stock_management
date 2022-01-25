package com.bakholdin.stock_management.service;

import java.util.Set;

public interface StockRatingDelegate<T>{
    Set<T> fetchRows();
}
