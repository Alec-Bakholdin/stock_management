package com.bakholdin.stock_management.repository;

import com.bakholdin.stock_management.model.StockManagementRowId;
import com.bakholdin.stock_management.model.TipRanksRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipRanksRepository extends JpaRepository<TipRanksRow, StockManagementRowId> {
}