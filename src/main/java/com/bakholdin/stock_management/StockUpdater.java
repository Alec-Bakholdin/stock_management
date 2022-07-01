package com.bakholdin.stock_management;

import com.bakholdin.stock_management.model.TipRanksRow;
import com.bakholdin.stock_management.model.YahooRow;
import com.bakholdin.stock_management.model.ZacksRow;
import com.bakholdin.stock_management.service.SendGridAdapter;
import com.bakholdin.stock_management.service.StockRatingDelegate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.Collection;

@Log4j2
@Component
@RequiredArgsConstructor
public class StockUpdater {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final StockRatingDelegate<ZacksRow> zacksStockRatingDelegate;
    private final StockRatingDelegate<TipRanksRow> tipRanksStockRatingDelegate;
    private final StockRatingDelegate<YahooRow> yahooRowStockRatingDelegate;
    private final Environment environment;
    private final SendGridAdapter sendGridAdapter;


    @PreDestroy
    public void preDestroyRoutine() {

    }

    @EventListener(ApplicationReadyEvent.class)
    public void updateOnStartupIfDev() {
        if(profileIsActive("dev")) {
            updateStocks(true);
        }
    }


    @Schedules({
            @Scheduled(cron = "0 0 13,17 * * *"),
            @Scheduled(cron = "0 30 3 * * *"),
    })
    public void updateOnScheduleIfProd() {
        if(profileIsActive("prod")) {
            updateStocks(true);
        }
    }

    private boolean profileIsActive(String profileStr) {
        return Arrays.asList(environment.getActiveProfiles()).contains(profileStr);
    }

    private void updateStocks(boolean sendEmails) {
        StockUpdaterExceptions updateExceptions = StockUpdaterExceptions.builder()
                .zacksException(fetchAndSaveZacksData())
                .tipRanksException(fetchAndSaveTipRanksData())
                .yahooException(fetchAndSaveYahooData())
                .build();
        if (sendEmails) {
            sendEmail(updateExceptions);
        }
    }

    private void sendEmail(StockUpdaterExceptions updateExceptions){
        if(updateExceptions.hasNoExceptions()) {
            sendGridAdapter.sendMessageFromServer(
                    "alecbakholdin@gmail.com",
                    "Successful Run",
                    "All updates were successful");
        } else {
            String messagePayload = "";
            try {
                MAPPER.writeValueAsString(updateExceptions);
            } catch (JsonProcessingException e) {
                messagePayload = e.getMessage() + ":\n" + Arrays.toString(e.getStackTrace());
            }

            sendGridAdapter.sendMessageFromServer(
                    "alecbakholdin@gmail.com",
                    "Failed Run",
                    messagePayload
            );
        }
    }

    private Exception fetchAndSaveYahooData() {
        try {
            log.info("Fetching Yahoo Data...");
            Collection<YahooRow> yahooRows = yahooRowStockRatingDelegate.fetchRows();
            log.info("Saving Yahoo Data...");
            yahooRowStockRatingDelegate.saveRows(yahooRows);
            log.info("Done");
        } catch(Exception e) {
            return e;
        }
        return null;
    }

    private Exception fetchAndSaveTipRanksData() {
        try {
            log.info("Fetching TipRanks Data...");
            Collection<TipRanksRow> rows2 = tipRanksStockRatingDelegate.fetchRows();
            log.info("Saving TipRanks data");
            tipRanksStockRatingDelegate.saveRows(rows2);
            log.info("Saved TipRanks Data");
        } catch (Exception e) {
            return e;
        }
        return null;
    }

    private Exception fetchAndSaveZacksData() {
        try {
            log.info("Fetching Zacks Data...");
            Collection<ZacksRow> rows = zacksStockRatingDelegate.fetchRows();
            log.info(String.format("Saving %d Zacks entries", rows.size()));
            zacksStockRatingDelegate.saveRows(rows);
            log.info("Saved all Zacks entries");
        } catch (Exception e) {
            return e;
        }
        return null;
    }
}