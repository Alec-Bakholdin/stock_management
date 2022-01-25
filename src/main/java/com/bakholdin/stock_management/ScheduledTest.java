package com.bakholdin.stock_management;

import com.bakholdin.stock_management.model.ZacksRow;
import com.bakholdin.stock_management.service.ZacksStockRatingDelegateImpl;
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

    @Scheduled(initialDelay = 3000, fixedRate = 1000000)
    public void testZacks() {
        log.info("Starting zacks execution");
        Collection<ZacksRow> rows = zacksStockRatingDelegate.fetchRows();
        for(var row : rows) {
            log.info(row);
        }
    }
}
