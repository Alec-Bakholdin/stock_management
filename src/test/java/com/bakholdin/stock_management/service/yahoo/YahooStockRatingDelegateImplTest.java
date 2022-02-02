package com.bakholdin.stock_management.service.yahoo;

import com.bakholdin.stock_management.config.ApplicationProperties;
import com.bakholdin.stock_management.config.YahooProperties;
import com.bakholdin.stock_management.model.CompanyRow;
import com.bakholdin.stock_management.model.PerformanceOutlook;
import com.bakholdin.stock_management.model.StockManagementRowId;
import com.bakholdin.stock_management.model.YahooRow;
import com.bakholdin.stock_management.repository.CompanyRepository;
import com.bakholdin.stock_management.repository.YahooRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.bakholdin.stock_management.RestTemplateTestUtils.createMockResponseEntity;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Log4j2
@ExtendWith(SpringExtension.class)
class YahooStockRatingDelegateImplTest {
    private static final String TICKER_1 = "AMRC";
    private static final String TICKER_2 = "AAPL";
    private static final String YAHOO_URL_FORMAT = "this_url_{symbol}";
    private static final String YAHOO_URL_1 = YAHOO_URL_FORMAT.replace("{symbol}", TICKER_1);
    private static final String YAHOO_URL_2 = YAHOO_URL_FORMAT.replace("{symbol}", TICKER_2);
    private static final double REQUESTS_PER_SECOND = 1.0;

    @Value("classpath:yahoo/AMRC.html")
    private Resource ticker1Response;
    @Value("classpath:yahoo/AAPL.html")
    private Resource ticker2Response;

    @Spy
    @InjectMocks
    private YahooStockRatingDelegateImpl yahooStockRatingDelegate;

    @Mock
    private ApplicationProperties applicationProperties;
    @Mock
    private YahooRepository yahooRepository;
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private YahooHtmlParser yahooHtmlParser;

    private InOrder inOrderObj;

    private void setupStringRestTemplateExchange(String url, ResponseEntity<String> responseEntity) {
        when(restTemplate.exchange(
                url == null ? Mockito.anyString() : Mockito.eq(url),
                Mockito.any(HttpMethod.class),
                Mockito.any(),
                Mockito.<Class<String>>any()
        )).thenReturn(responseEntity);
    }

    private void verifyRestTemplateExchangeOccurred(String url, int numInvocations) {
        inOrderObj.verify(restTemplate, times(numInvocations)).exchange(
                url == null ? Mockito.anyString() : Mockito.eq(url),
                Mockito.any(HttpMethod.class),
                Mockito.any(),
                Mockito.<Class<String>>any()
        );
    }

    @BeforeEach
    void setup() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        YahooProperties yahooProperties = mock(YahooProperties.class);
        when(yahooProperties.getTickerQuoteUrlFormat()).thenReturn(YAHOO_URL_FORMAT);
        when(yahooProperties.getRequestsPerSecond()).thenReturn(REQUESTS_PER_SECOND);
        when(applicationProperties.getYahoo()).thenReturn(yahooProperties);

        CompanyRow row1 = CompanyRow.builder().symbol(TICKER_1).build();
        CompanyRow row2 = CompanyRow.builder().symbol(TICKER_2).build();
        List<CompanyRow> listOfRows = Arrays.asList(row1, row2);
        when(companyRepository.findAll()).thenReturn(listOfRows);

        inOrderObj = inOrder(restTemplate, companyRepository, yahooRepository);

        Method postConstruct = YahooStockRatingDelegateImpl.class.getDeclaredMethod("initRateLimiter");
        postConstruct.setAccessible(true);
        postConstruct.invoke(yahooStockRatingDelegate);
    }

    @Test
    void fetchRowsParsesRowCorrectly() throws IOException {
        YahooRow row1 = getYahooRow(TICKER_1);
        String ticker1ResponseStr = Files.readString(Path.of(ticker1Response.getFile().getPath()));
        ResponseEntity<String> responseEntity1 = createMockResponseEntity(HttpStatus.OK, ticker1ResponseStr);
        setupStringRestTemplateExchange(YAHOO_URL_1, responseEntity1);
        when(yahooHtmlParser.parseHtml(TICKER_1, ticker1ResponseStr)).thenReturn(row1);

        YahooRow row2 = getYahooRow(TICKER_2);
        String ticker2ResponseStr = Files.readString(Path.of(ticker2Response.getFile().getPath()));
        ResponseEntity<String> responseEntity2 = createMockResponseEntity(HttpStatus.OK, ticker2ResponseStr);
        setupStringRestTemplateExchange(YAHOO_URL_2, responseEntity2);
        when(yahooHtmlParser.parseHtml(TICKER_2, ticker2ResponseStr)).thenReturn(row2);

        List<YahooRow> yahooRowList = yahooStockRatingDelegate.fetchRows();

        List<YahooRow> expectedList = Arrays.asList(row1, row2);
        Assertions.assertEquals(expectedList, yahooRowList);

        inOrderObj.verify(companyRepository).findAll();
        verifyRestTemplateExchangeOccurred(YAHOO_URL_1, 1);
        verifyRestTemplateExchangeOccurred(YAHOO_URL_2, 1);
    }

    @Test
    void fetchRowsThrowsExceptionWhenResponseIsNot200() {
        ResponseEntity<String> responseEntity = createMockResponseEntity(HttpStatus.BAD_REQUEST, "");
        setupStringRestTemplateExchange(null, responseEntity);

        Assertions.assertThrows(IllegalArgumentException.class, () -> yahooStockRatingDelegate.fetchRows());

        verifyRestTemplateExchangeOccurred(YAHOO_URL_1, 1);
        verifyRestTemplateExchangeOccurred(YAHOO_URL_2, 0);
    }

    @Test
    @SuppressWarnings("unchecked")
    void saveRowsSavesRows() {
        List<YahooRow> mockList = mock(List.class);

        yahooStockRatingDelegate.saveRows(mockList);

        verify(yahooRepository).saveAll(mockList);
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