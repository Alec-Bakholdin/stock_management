package com.bakholdin.stock_management.service.TipRanks;

import com.bakholdin.stock_management.config.ApplicationProperties;
import com.bakholdin.stock_management.config.TipRanksProperties;
import com.bakholdin.stock_management.model.CompanyRow;
import com.bakholdin.stock_management.model.StockManagementRowId;
import com.bakholdin.stock_management.model.TipRanksRow;
import com.bakholdin.stock_management.repository.CompanyRepository;
import com.bakholdin.stock_management.repository.TipRanksRepository;
import com.bakholdin.stock_management.service.TipRanks.model.AnalystConsensus;
import com.bakholdin.stock_management.service.TipRanks.model.NewsSentiment;
import com.bakholdin.stock_management.service.TipRanks.model.StockData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.bakholdin.stock_management.RestTemplateTestUtils.createMockResponseEntity;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class TipRanksStockRatingDelegateImplTest {
    private static final String TICKER_1 = "AA";
    private static final String TICKER_2 = "AAPL";
    private static final String STOCK_DATA_URL = "stock_data_url";
    private static final String NEWS_SENTIMENT_URL = "news_sentiment_url";
    private final static String FULL_STOCK_DATA_URL = String.format("%s?tickers=%s,%s", STOCK_DATA_URL, TICKER_1, TICKER_2);
    private final static String FULL_NEWS_SENTIMENT_URL = String.format("%s?tickers=%s,%s", NEWS_SENTIMENT_URL, TICKER_1, TICKER_2);


    @Spy
    @InjectMocks
    private TipRanksStockRatingDelegateImpl tipRanksStockRatingDelegate;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private TipRanksRepository tipRanksRepository;
    @Mock
    private ApplicationProperties applicationProperties;
    @Mock
    private TipRanksProperties tipRanksProperties;

    private InOrder inOrderObj;

    @BeforeEach
    void setup() {
        when(applicationProperties.getTipRanks()).thenReturn(tipRanksProperties);
        when(tipRanksProperties.getStockDataUrl()).thenReturn(STOCK_DATA_URL);
        when(tipRanksProperties.getNewsSentimentUrl()).thenReturn(NEWS_SENTIMENT_URL);

        CompanyRow row1 = CompanyRow.builder().symbol(TICKER_1).build();
        CompanyRow row2 = CompanyRow.builder().symbol(TICKER_2).build();
        List<CompanyRow> listOfRows = Arrays.asList(row1, row2);
        when(companyRepository.findAll()).thenReturn(listOfRows);

        inOrderObj = inOrder(restTemplate, companyRepository, tipRanksRepository);
    }

    private <T> void setMockRestTemplateResponse(String url, ResponseEntity<T> response) {
        when(restTemplate.exchange(
                url == null ? Mockito.anyString() : Mockito.eq(url),
                Mockito.any(HttpMethod.class),
                Mockito.any(),
                Mockito.<ParameterizedTypeReference<T>>any()
        )).thenReturn(response);
    }

    private void verifyRestTemplateExchangeOccurred(String url, int numInvocations) {
        inOrderObj.verify(restTemplate, times(numInvocations)).exchange(
                url == null ? Mockito.anyString() : Mockito.eq(url),
                Mockito.any(HttpMethod.class),
                Mockito.any(),
                Mockito.<ParameterizedTypeReference<?>>any()
        );
    }

    @Test
    void fetchRowsGetsDataAndMergesIntoTipRanksRow() {
        List<StockData> stockDataExchangeList = Arrays.asList(getStockData(TICKER_1), getStockData(TICKER_2));
        List<NewsSentiment> newsSentimentExchangeList = Arrays.asList(getNewsSentiment(TICKER_2), getNewsSentiment(TICKER_1));
        ResponseEntity<List<StockData>> stockDataResponseEntity = createMockResponseEntity(HttpStatus.OK, stockDataExchangeList);
        ResponseEntity<List<NewsSentiment>> newsSentimentResponseEntity = createMockResponseEntity(HttpStatus.OK, newsSentimentExchangeList);
        setMockRestTemplateResponse(FULL_STOCK_DATA_URL, stockDataResponseEntity);
        setMockRestTemplateResponse(FULL_NEWS_SENTIMENT_URL, newsSentimentResponseEntity);
        List<TipRanksRow> tipRanksRowExpectedList = Arrays.asList(getTipRanksRow(TICKER_1), getTipRanksRow(TICKER_2));

        List<TipRanksRow> tipRanksRowActualList = tipRanksStockRatingDelegate.fetchRows();

        Assertions.assertEquals(tipRanksRowExpectedList, tipRanksRowActualList);
        inOrderObj.verify(companyRepository).findAll();
        verifyRestTemplateExchangeOccurred(FULL_STOCK_DATA_URL, 1);
        verifyRestTemplateExchangeOccurred(FULL_NEWS_SENTIMENT_URL, 1);
    }

    @Test
    void fetchRowsThrowsErrorOnNon200ResponseFromStockData() {
        ResponseEntity<List<StockData>> responseEntity = createMockResponseEntity(HttpStatus.BAD_REQUEST, null);
        setMockRestTemplateResponse(FULL_STOCK_DATA_URL, responseEntity);

        Assertions.assertThrows(IllegalArgumentException.class, () -> tipRanksStockRatingDelegate.fetchRows());

        verifyRestTemplateExchangeOccurred(FULL_STOCK_DATA_URL, 1);
        verifyRestTemplateExchangeOccurred(FULL_NEWS_SENTIMENT_URL, 0);
    }

    @Test
    void fetchRowsThrowsErrorOnNon200ResponseFromNewsSentiment() {
        ResponseEntity<List<StockData>> stockDataResponseEntity = createMockResponseEntity(HttpStatus.OK, Collections.emptyList());
        setMockRestTemplateResponse(FULL_STOCK_DATA_URL, stockDataResponseEntity);
        ResponseEntity<List<StockData>> newsSentimentResponseEntity = createMockResponseEntity(HttpStatus.BAD_REQUEST, null);
        setMockRestTemplateResponse(FULL_NEWS_SENTIMENT_URL, newsSentimentResponseEntity);

        Assertions.assertThrows(IllegalArgumentException.class, () -> tipRanksStockRatingDelegate.fetchRows());

        verifyRestTemplateExchangeOccurred(FULL_STOCK_DATA_URL, 1);
        verifyRestTemplateExchangeOccurred(FULL_NEWS_SENTIMENT_URL, 1);
    }

    private TipRanksRow getTipRanksRow(String ticker) {
        if(!Objects.equals(ticker, TICKER_1) && !Objects.equals(ticker, TICKER_2)) {
            throw new UnsupportedOperationException(ticker + " is not a supported ticker");
        }
        boolean isTickerOne = Objects.equals(ticker, TICKER_1);

        CompanyRow row = CompanyRow.builder().symbol(ticker).build();
        StockManagementRowId rowId = StockManagementRowId.builder().companyRow(row).build();
        return TipRanksRow.builder()
                .stockManagementRowId(rowId)
                .analystConsensus(isTickerOne ? "strongbuy" : "strongsell")
                .bestAnalystConsensus(isTickerOne ? "strongbuy" : "strongsell")
                .analystPriceTarget(isTickerOne ? 1.0 : 0)
                .bestAnalystPriceTarget(isTickerOne ? 1.3 : 0.3)
                .estimatedDividendYield(isTickerOne ? 1.6 : 0.6)
                .newsSentiment(isTickerOne ? 1.9 : 0.9)
                .build();
    }

    private StockData getStockData(String ticker) {
        if(!Objects.equals(ticker, TICKER_1) && !Objects.equals(ticker, TICKER_2)) {
            throw new UnsupportedOperationException(ticker + " is not a supported ticker");
        }
        boolean isTickerOne = Objects.equals(ticker, TICKER_1);

        return StockData.builder()
                .ticker(ticker)
                .analystConsensus(new AnalystConsensus(isTickerOne ? "strongbuy" : "strongsell"))
                .bestAnalystConsensus(new AnalystConsensus(isTickerOne ? "strongbuy" : "strongsell"))
                .analystPriceTarget(isTickerOne ? 1.0 : 0)
                .bestAnalystPriceTarget(isTickerOne ? 1.3 : 0.3)
                .estimatedDividendYield(isTickerOne ? 1.6 : 0.6)
                .build();
    }

    private NewsSentiment getNewsSentiment(String ticker) {
        if(!Objects.equals(ticker, TICKER_1) && !Objects.equals(ticker, TICKER_2)) {
            throw new UnsupportedOperationException(ticker + " is not a supported ticker");
        }
        boolean isTickerOne = Objects.equals(ticker, TICKER_1);

        return NewsSentiment.builder()
                .ticker(ticker)
                .newsSentiment(isTickerOne ? 1.9 : 0.9)
                .build();
    }
}