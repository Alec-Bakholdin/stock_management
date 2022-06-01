package com.bakholdin.stock_management;

import com.bakholdin.stock_management.model.TipRanksRow;
import com.bakholdin.stock_management.model.YahooRow;
import com.bakholdin.stock_management.model.ZacksRow;
import com.bakholdin.stock_management.service.StockRatingDelegate;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Log4j2
@Component
@RequiredArgsConstructor
public class StockUpdater {
    private final StockRatingDelegate<ZacksRow> zacksStockRatingDelegate;
    private final StockRatingDelegate<TipRanksRow> tipRanksStockRatingDelegate;
    private final StockRatingDelegate<YahooRow> yahooRowStockRatingDelegate;

    @Scheduled(initialDelay = 1000, fixedRate = 1000000)
    public void UpdateStocks() {
        log.info("Fetching Zacks Data...");
        Collection<ZacksRow> rows = zacksStockRatingDelegate.fetchRows();
        log.info(String.format("Saving %d Zacks entries", rows.size()));
        zacksStockRatingDelegate.saveRows(rows);
        log.info("Saved all Zacks entries");

        log.info("Fetching TipRanks Data...");
        Collection<TipRanksRow> rows2 = tipRanksStockRatingDelegate.fetchRows();
        log.info("Saving TipRanks data");
        //tipRanksStockRatingDelegate.saveRows(rows2);
        log.info("Saved TipRanks Data");

        log.info("Fetching Yahoo Data...");
        //Collection<YahooRow> yahooRows = yahooRowStockRatingDelegate.fetchRows();
        log.info("Saving Yahoo Data...");
        //yahooRowStockRatingDelegate.saveRows(yahooRows);
        log.info("Done");
    }
}