package com.bakholdin.stock_management.model;

import com.univocity.parsers.annotations.Nested;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Embeddable
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockManagementRowId implements Serializable {
    @ManyToOne
    @JoinColumn(name="symbol")
    @Nested
    private CompanyRow companyRow;
    @Column
    private LocalDate dateRetrieved = LocalDate.now();

    public StockManagementRowId(String symbol) {
        companyRow = CompanyRow.builder()
                .symbol(symbol)
                .build();
    }
}