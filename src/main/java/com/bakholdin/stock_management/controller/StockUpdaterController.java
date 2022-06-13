package com.bakholdin.stock_management.controller;

import com.bakholdin.stock_management.StockUpdater;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
public class StockUpdaterController {
    private final StockUpdater stockUpdater;

    @PostMapping("/update")
    public String updateStocks() {
        log.info("triggered");
        return "Stock update triggered";
    }
}
