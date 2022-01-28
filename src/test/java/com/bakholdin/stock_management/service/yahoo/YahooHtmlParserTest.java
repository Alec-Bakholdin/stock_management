package com.bakholdin.stock_management.service.yahoo;

import com.bakholdin.stock_management.model.PerformanceOutlook;
import com.bakholdin.stock_management.model.StockManagementRowId;
import com.bakholdin.stock_management.model.YahooRow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@ExtendWith(SpringExtension.class)
class YahooHtmlParserTest {
    private static final String TICKER_1 = "AMRC";
    private static final String TICKER_2 = "AAPL";
    @Value("classpath:yahoo/AMRC.html")
    private Resource ticker1Response;
    @Value("classpath:yahoo/AAPL.html")
    private Resource ticker2Response;

    private final YahooHtmlParser yahooHtmlParser = new YahooHtmlParser();

    @Test
    void parseTicker1Works() throws IOException {
        String responseStr = Files.readString(Path.of(ticker1Response.getFile().getPath()));

        YahooRow actual = yahooHtmlParser.parseHtml(TICKER_1, responseStr);

        Assertions.assertEquals(getYahooRow(TICKER_1), actual);
    }

    @Test
    void parseTicker2Works() throws IOException {
        String responseStr = Files.readString(Path.of(ticker2Response.getFile().getPath()));

        YahooRow actual = yahooHtmlParser.parseHtml(TICKER_2, responseStr);

        Assertions.assertEquals(getYahooRow(TICKER_2), actual);
    }

    private YahooRow getYahooRow(String symbol) {
        boolean isTicker1 = Objects.equals(symbol, TICKER_1);
        boolean isTicker2 = Objects.equals(symbol, TICKER_2);
        if(!isTicker1 && !isTicker2) {
            throw new UnsupportedOperationException("Invalid symbol");
        }

        return YahooRow.builder()
                .stockManagementRowId(new StockManagementRowId(symbol))
                .estimatedReturn(isTicker1 ? 2.0 : -14.0)
                .fairValue(isTicker1 ? "Near Fair Value" : "Overvalued")
                .shortTerm(isTicker1 ? PerformanceOutlook.Bullish : PerformanceOutlook.Neutral)
                .midTerm(PerformanceOutlook.Bearish)
                .longTerm(isTicker1 ? PerformanceOutlook.Bearish : PerformanceOutlook.Neutral)
                .build();
    }
}