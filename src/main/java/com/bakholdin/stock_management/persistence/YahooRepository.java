package com.bakholdin.stock_management.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YahooRepository extends JpaRepository<YahooRow, StockManagementRowId> {
}