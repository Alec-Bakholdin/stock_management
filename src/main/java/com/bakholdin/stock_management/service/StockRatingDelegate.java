package com.bakholdin.stock_management.service;

import java.util.Collection;

public interface StockRatingDelegate<T>{
    Collection<T> fetchRows();
}
