package com.bakholdin.stock_management.service.Zacks;

import com.bakholdin.stock_management.config.ApplicationProperties;
import com.bakholdin.stock_management.config.ZacksProperties;
import com.bakholdin.stock_management.model.CompanyRow;
import com.bakholdin.stock_management.model.StockManagementRowId;
import com.bakholdin.stock_management.model.ZacksRow;
import com.bakholdin.stock_management.repository.CompanyRepository;
import com.bakholdin.stock_management.repository.ZacksRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ZacksStockRatingDelegateImplTest {
    private static final String HOLDINGS_URL = "holdings_url";
    private static final MultiValueMap<String, String> EMPTY_MAP = new LinkedMultiValueMap<>();
    private static final String CSV_HEADERS = "Symbol,Company,Price,Shares,$Chg,%Chg,Industry Rank,Zacks Rank,Value Score,Growth Score,Momentum Score,VGM Score";
    private static final String CSV_FULL_ROW = "\"AA\",\"Alcoa\",\"5,823.02\",\"0\",\"1.81\",\"3.22\",\"38\",\"3\",\"A\",\"B\",\"C\",\"D\"";
    private static final String CSV_NULL_ROW =  "\"ARKQ\",\"NA\",\"NA\",\"NA\",\"NA\",\"NA\",\"NA\",\"NA\",\"NA\",\"NA\",\"NA\",\"NA\"";

    @InjectMocks
    private ZacksStockRatingDelegateImpl zacksStockRatingDelegate;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ApplicationProperties applicationProperties;
    @Mock
    private ZacksProperties zacksProperties;

    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private ZacksRepository zacksRepository;

    @BeforeEach
    private void setup() {
        when(zacksProperties.getHoldingsUrl()).thenReturn(HOLDINGS_URL);
        when(zacksProperties.getBody()).thenReturn(EMPTY_MAP);
        when(zacksProperties.getHeaders()).thenReturn(EMPTY_MAP);
        when(applicationProperties.getZacks()).thenReturn(zacksProperties);
    }

    @SuppressWarnings("unchecked")
    private <T> ResponseEntity<T> getMockResponseEntity(HttpStatus status, T body) {
        ResponseEntity<T> responseEntity = mock(ResponseEntity.class);
        when(responseEntity.getStatusCode()).thenReturn(status);
        when(responseEntity.getStatusCodeValue()).thenReturn(status.value());
        when(responseEntity.hasBody()).thenReturn(body != null);
        when(responseEntity.getBody()).thenReturn(body);
        return responseEntity;
    }

    private <T> void mockRestTemplateExchange(ResponseEntity<T> response) {
        when(restTemplate.exchange(
                Mockito.anyString(),
                Mockito.any(HttpMethod.class),
                Mockito.any(),
                Mockito.<Class<T>>any()
        )).thenReturn(response);
    }

    @Test
    void fetchRowsThrowsExceptionOnNot200() {
        ResponseEntity<String> failedResponse = getMockResponseEntity(HttpStatus.BAD_REQUEST, null);
        mockRestTemplateExchange(failedResponse);

        Assertions.assertThrows(IllegalArgumentException.class, () -> zacksStockRatingDelegate.fetchRows());
    }

    @Test
    void fetchRowsThrowsExceptionOnEmptyBody() {
        ResponseEntity<String> emptyBodyResponse = getMockResponseEntity(HttpStatus.OK, null);
        when(emptyBodyResponse.getStatusCode()).thenReturn(HttpStatus.OK);
        when(emptyBodyResponse.getStatusCodeValue()).thenReturn(HttpStatus.OK.value());
        when(emptyBodyResponse.hasBody()).thenReturn(false);
        mockRestTemplateExchange(emptyBodyResponse);

        Assertions.assertThrows(IllegalArgumentException.class, () -> zacksStockRatingDelegate.fetchRows());
    }

    @Test
    void fetchRowsParsesFullRowCorrectly() {
        String responseBody = String.format("%s\n%s\n", CSV_HEADERS, CSV_FULL_ROW);
        ResponseEntity<String> fullRowResponse = getMockResponseEntity(HttpStatus.OK, responseBody);
        mockRestTemplateExchange(fullRowResponse);

        List<ZacksRow> parsedRows = zacksStockRatingDelegate.fetchRows();
        ZacksRow row = parsedRows.get(0);

        validateFullRow(row);
    }

    void validateFullRow(ZacksRow row) {
        Assertions.assertEquals("AA", row.getId().getCompanyRow().getSymbol());
        Assertions.assertEquals(LocalDate.now(), row.getId().getDateRetrieved());
        Assertions.assertEquals("Alcoa", row.getId().getCompanyRow().getCompanyName());
        Assertions.assertEquals(5823.02, row.getId().getCompanyRow().getLatestPrice());
        Assertions.assertEquals(5823.02, row.getPrice());
        Assertions.assertEquals(3, row.getZacksRank());
        Assertions.assertEquals(38, row.getIndustryRank());
        Assertions.assertEquals('A', row.getValueScore());
        Assertions.assertEquals('B', row.getGrowthScore());
        Assertions.assertEquals('C', row.getMomentumScore());
        Assertions.assertEquals('D', row.getVgmScore());
    }

    @Test
    void fetchRowParsesNullValuesCorrectly() {
        String responseBody = String.format("%s\n%s\n", CSV_HEADERS, CSV_NULL_ROW);
        ResponseEntity<String> fullRowResponse = getMockResponseEntity(HttpStatus.OK, responseBody);
        mockRestTemplateExchange(fullRowResponse);

        List<ZacksRow> parsedRows = zacksStockRatingDelegate.fetchRows();
        ZacksRow row = parsedRows.get(0);

        validateNullRow(row);
    }

    void validateNullRow(ZacksRow row) {
        Assertions.assertEquals("ARKQ", row.getId().getCompanyRow().getSymbol());
        Assertions.assertEquals(LocalDate.now(), row.getId().getDateRetrieved());
        Assertions.assertNull(row.getId().getCompanyRow().getCompanyName());
        Assertions.assertNull(row.getId().getCompanyRow().getLatestPrice());
        Assertions.assertNull(row.getPrice());
        Assertions.assertNull(row.getZacksRank());
        Assertions.assertNull(row.getIndustryRank());
        Assertions.assertNull(row.getValueScore());
        Assertions.assertNull(row.getGrowthScore());
        Assertions.assertNull(row.getMomentumScore());
        Assertions.assertNull(row.getVgmScore());
    }

    @Test
    void fetchRowsParsesMultipleRowsSimultaneously() {
        String responseBody = String.format("%s\n%s\n%s\n", CSV_HEADERS, CSV_FULL_ROW, CSV_NULL_ROW);
        ResponseEntity<String> fullRowResponse = getMockResponseEntity(HttpStatus.OK, responseBody);
        mockRestTemplateExchange(fullRowResponse);

        List<ZacksRow> parsedRows = zacksStockRatingDelegate.fetchRows();

        validateFullRow(parsedRows.get(0));
        validateNullRow(parsedRows.get(1));
    }

    @Test
    void saveRowsSavesCompaniesThenZacksRows() {
        CompanyRow companyRow = CompanyRow.builder()
                .symbol("AA")
                .companyName("Some name")
                .build();
        Set<CompanyRow> companyRowSet = Collections.singleton(companyRow);
        ZacksRow zacksRow = ZacksRow.builder()
                .id(new StockManagementRowId(companyRow, LocalDate.now()))
                .build();
        List<ZacksRow> zacksRowList = Collections.singletonList(zacksRow);


        zacksStockRatingDelegate.saveRows(zacksRowList);

        InOrder inOrder = inOrder(companyRepository, zacksRepository);
        inOrder.verify(companyRepository).saveAll(companyRowSet);
        inOrder.verify(zacksRepository).saveAll(zacksRowList);


    }
}