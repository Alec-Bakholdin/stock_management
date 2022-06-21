package com.bakholdin.stock_management.service.yahoo;

import com.bakholdin.stock_management.config.properties.ApplicationProperties;
import com.bakholdin.stock_management.model.CompanyRow;
import com.bakholdin.stock_management.model.YahooRow;
import com.bakholdin.stock_management.repository.CompanyRepository;
import com.bakholdin.stock_management.repository.YahooRepository;
import com.bakholdin.stock_management.service.StockRatingDelegate;
import com.google.common.util.concurrent.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
@Log4j2
@Service
@RequiredArgsConstructor
public class YahooStockRatingDelegateImpl implements StockRatingDelegate<YahooRow> {

    private final ApplicationProperties applicationProperties;
    private final YahooRepository yahooRepository;
    private final CompanyRepository companyRepository;
    private final RestTemplate restTemplate;
    private final YahooHtmlParser yahooHtmlParser;

    private RateLimiter rateLimiter;
    @PostConstruct
    private void initRateLimiter() {
        rateLimiter = RateLimiter.create(applicationProperties.getYahoo().getRequestsPerSecond());
    }

    @Override
    public List<YahooRow> fetchRows() {
        List<String> symbolList = getTargetSymbols();
        List<YahooRow> yahooRows = new ArrayList<>();

        List<String> processedSymbolList = new ArrayList<>();
        int logGranularity = 10; // number of rows to process before logging.

        for(int i = 0; i < symbolList.size(); i++) {
            String symbol = symbolList.get(i);
            YahooRow yahooRow = getYahooRowFromSymbol(symbol);
            yahooRows.add(yahooRow);
            processedSymbolList.add(symbol);

            if(processedSymbolList.size() >= logGranularity || i >= symbolList.size() - 1) {
                String processedSymbolStr = String.join(", ", processedSymbolList);
                log.info("Processed {} for a total of {}/{} symbols", processedSymbolStr, i + 1, symbolList.size());
                processedSymbolList.clear();
            }
        }
        return yahooRows;
    }

    private YahooRow getYahooRowFromSymbol(String symbol) {
        String symbolHtml = getSymbolHtml(symbol);
        return yahooHtmlParser.parseHtml(symbol, symbolHtml);
    }

    private String getSymbolHtml(String symbol) {
        rateLimiter.acquire();
        String urlFormat = applicationProperties.getYahoo().getTickerQuoteUrlFormat();
        ResponseEntity<String> response = restTemplate.exchange(urlFormat.replace("{symbol}", symbol), HttpMethod.GET, null, String.class);
        Assert.isTrue(response.getStatusCode() == HttpStatus.OK, String.format("Error %d %s from Yahoo for symbol %s", response.getStatusCodeValue(), response.getStatusCode(), symbol));
        return response.getBody();
    }

    private List<String> getTargetSymbols() {
        List<CompanyRow> companyRowList = companyRepository.findAll();
        return companyRowList.stream()
                .map(CompanyRow::getSymbol)
                .collect(Collectors.toList());
    }

    @Override
    public void saveRows(Collection<YahooRow> rows) {
        yahooRepository.saveAll(rows);
    }
}
