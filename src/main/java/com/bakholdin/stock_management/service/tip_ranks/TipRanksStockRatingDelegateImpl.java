package com.bakholdin.stock_management.service.tip_ranks;

import com.bakholdin.stock_management.config.ApplicationProperties;
import com.bakholdin.stock_management.config.TipRanksProperties;
import com.bakholdin.stock_management.dto.TipRanksDto;
import com.bakholdin.stock_management.mapper.TipRanksMapper;
import com.bakholdin.stock_management.model.CompanyRow;
import com.bakholdin.stock_management.model.TipRanksRow;
import com.bakholdin.stock_management.repository.CompanyRepository;
import com.bakholdin.stock_management.repository.TipRanksRepository;
import com.bakholdin.stock_management.service.StockRatingDelegate;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TipRanksStockRatingDelegateImpl implements StockRatingDelegate<TipRanksRow> {
    private final ApplicationProperties applicationProperties;
    private final RestTemplate restTemplate;
    private final CompanyRepository companyRepository;
    private final TipRanksRepository tipRanksRepository;
    private final TipRanksMapper tipRanksMapper;

    @Override
    public List<TipRanksRow> fetchRows() {
        TipRanksProperties tipRanksProperties = applicationProperties.getTipRanks();

        String tickersStr = getTickersStr();
        List<TipRanksDto> tipRanksDtos = getDataByTicker(
                tipRanksProperties.getStockDataUrl(),
                tickersStr,
                new ParameterizedTypeReference<>() {});

        return tipRanksDtos.stream()
                .map(tipRanksMapper::fromDto)
                .collect(Collectors.toList());
    }

    private String getTickersStr() {
        List<CompanyRow> companyRows = companyRepository.findAll();
        return companyRows.stream()
                .filter(c -> !"SZKMY".equals(c.getSymbol())) // SZKMY is not in TipRanks and causes a null pointer exception for them
                .map(CompanyRow::getSymbol)
                .collect(Collectors.joining(","));
    }

    private <T> List<T> getDataByTicker(String url, String tickersStr, ParameterizedTypeReference<List<T>> type) {
        String fullStockDataUrl = String.format("%s?tickers=%s", url, tickersStr);
        ResponseEntity<List<T>> dataResponse = get(fullStockDataUrl, type);
        return Objects.requireNonNull(dataResponse.getBody());
    }

    private <T> ResponseEntity<T> get(String url, ParameterizedTypeReference<T> type) {
        ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, type);
        validateResponse(url, responseEntity);
        return responseEntity;
    }

    private <T> void validateResponse(String url, ResponseEntity<T> responseEntity) {
        HttpStatus statusCode = responseEntity.getStatusCode();
        String statusErrorResponse = String.format("RestTemplate call to %s failed with %d %s", url, statusCode.value(), statusCode);
        Assert.isTrue(responseEntity.getStatusCode() == HttpStatus.OK, statusErrorResponse);
    }

    @Override
    public void saveRows(Collection<TipRanksRow> rows) {
        tipRanksRepository.saveAll(rows);
    }
}
