package com.bakholdin.stock_management.repository;

import com.bakholdin.stock_management.model.CompanyRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyRow, String> {
}