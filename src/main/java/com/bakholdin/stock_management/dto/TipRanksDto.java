package com.bakholdin.stock_management.dto;

import lombok.Data;

import java.util.List;

@Data
public class TipRanksDto {
    private List<TipRanksStockDataDto> data;
}
