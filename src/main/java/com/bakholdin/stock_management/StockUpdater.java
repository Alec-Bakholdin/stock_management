package com.bakholdin.stock_management;

import com.bakholdin.stock_management.model.TipRanksRow;
import com.bakholdin.stock_management.model.YahooRow;
import com.bakholdin.stock_management.model.ZacksRow;
import com.bakholdin.stock_management.service.StockRatingDelegate;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;

@Log4j2
@Component
@RequiredArgsConstructor
public class StockUpdater {
    private final StockRatingDelegate<ZacksRow> zacksStockRatingDelegate;
    private final StockRatingDelegate<TipRanksRow> tipRanksStockRatingDelegate;
    private final StockRatingDelegate<YahooRow> yahooRowStockRatingDelegate;
    private final Environment environment;

    @EventListener(ApplicationReadyEvent.class)
    public void updateOnStartupIfDev() {
        if(profileIsActive("dev")) {
            updateStocks();
        }
    }


    @Schedules({
            @Scheduled(cron = "0 13,17 * * * *"),
            @Scheduled(cron = "30 3 * * * *"),
    })
    public void updateOnScheduleIfProd() {
        if(profileIsActive("prod")) {
            updateStocks();
        }
    }

    private boolean profileIsActive(String profileStr) {
        return Arrays.asList(environment.getActiveProfiles()).contains(profileStr);
    }

    private void updateStocks() {
        fetchAndSaveZacksData();
        fetchAndSaveTipRanksData();
        fetchAndSaveYahooData();
    }

    private void fetchAndSaveYahooData() {
        log.info("Fetching Yahoo Data...");
        Collection<YahooRow> yahooRows = yahooRowStockRatingDelegate.fetchRows();
        log.info("Saving Yahoo Data...");
        yahooRowStockRatingDelegate.saveRows(yahooRows);
        log.info("Done");
    }

    private void fetchAndSaveTipRanksData() {
        log.info("Fetching TipRanks Data...");
        Collection<TipRanksRow> rows2 = tipRanksStockRatingDelegate.fetchRows();
        log.info("Saving TipRanks data");
        tipRanksStockRatingDelegate.saveRows(rows2);
        log.info("Saved TipRanks Data");
    }

    private void fetchAndSaveZacksData() {
        log.info("Fetching Zacks Data...");
        Collection<ZacksRow> rows = zacksStockRatingDelegate.fetchRows();
        log.info(String.format("Saving %d Zacks entries", rows.size()));
        zacksStockRatingDelegate.saveRows(rows);
        log.info("Saved all Zacks entries");
    }
}