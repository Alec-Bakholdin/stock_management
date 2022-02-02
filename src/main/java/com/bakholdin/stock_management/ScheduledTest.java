package com.bakholdin.stock_management;

import com.bakholdin.stock_management.model.TipRanksRow;
import com.bakholdin.stock_management.model.ZacksRow;
import com.bakholdin.stock_management.service.TipRanks.TipRanksStockRatingDelegateImpl;
import com.bakholdin.stock_management.service.Zacks.ZacksStockRatingDelegateImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Log4j2
@Component
@RequiredArgsConstructor
public class ScheduledTest {
    private final ZacksStockRatingDelegateImpl zacksStockRatingDelegate;
    private final TipRanksStockRatingDelegateImpl tipRanksStockRatingDelegate;

    @Scheduled(initialDelay = 1000, fixedRate = 1000000)
    public void testZacks() {
        log.info("Fetching Zacks Data...");
        Collection<ZacksRow> rows = zacksStockRatingDelegate.fetchRows();
        log.info(String.format("Saving %d Zacks entries", rows.size()));
        zacksStockRatingDelegate.saveRows(rows);
        log.info("Fetching TipRanks Data...");
        Collection<TipRanksRow> rows2 = tipRanksStockRatingDelegate.fetchRows();
        for(var row : rows2) {
            log.info(row);
        }
        log.info("Saving TipRanks data");
    }
}