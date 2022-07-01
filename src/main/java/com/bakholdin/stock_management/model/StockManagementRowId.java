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
import javax.persistence.Transient;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Embeddable
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockManagementRowId implements Serializable {
    @ManyToOne
    @JoinColumn(name = "symbol")
    @Nested
    @Builder.ObtainVia(method = "createCompanyRow")
    private CompanyRow companyRow;
    @Column
    @Builder.Default
    private LocalDate dateRetrieved = LocalDate.now();

    @Transient
    private String symbol;

    public CompanyRow createCompanyRow() {
        if (companyRow != null) {
            return companyRow;
        } else if (symbol != null) {
            return CompanyRow.builder()
                    .symbol(symbol)
                    .build();
        }

        return null;
    }
}