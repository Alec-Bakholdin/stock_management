package com.bakholdin.stock_management.service.tip_ranks;

import com.bakholdin.stock_management.config.properties.ApplicationProperties;
import com.bakholdin.stock_management.config.properties.TipRanksProperties;
import com.bakholdin.stock_management.dto.TipRanksDto;
import com.bakholdin.stock_management.dto.TipRanksStockDataDto;
import com.bakholdin.stock_management.mapper.TipRanksMapper;
import com.bakholdin.stock_management.model.CompanyRow;
import com.bakholdin.stock_management.model.TipRanksRow;
import com.bakholdin.stock_management.repository.CompanyRepository;
import com.bakholdin.stock_management.repository.TipRanksRepository;
import com.bakholdin.stock_management.service.StockRatingDelegate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TipRanksStockRatingDelegateImpl implements StockRatingDelegate<TipRanksRow> {
    private final ApplicationProperties applicationProperties;
    private final RestTemplate restTemplate;
    private final CompanyRepository companyRepository;
    private final TipRanksRepository tipRanksRepository;
    private final TipRanksMapper tipRanksMapper;
    private final WebClient webClient;

    @Override
    public List<TipRanksRow> fetchRows() {
        TipRanksProperties tipRanksProperties = applicationProperties.getTipRanks();

        String tickersStr = getTickersStr();
        List<TipRanksStockDataDto> tipRanksStockDataDtos = getDataByTicker(
                tipRanksProperties.getStockDataUrl(),
                tickersStr);

        return tipRanksStockDataDtos.stream()
                .map(tipRanksMapper::fromDto)
                .collect(Collectors.toList());
    }

    private String getTickersStr() {
        List<CompanyRow> companyRows = companyRepository.findAll();
        Set<String> excludedTickers = applicationProperties.getTipRanks().getExcludedTickers();
        return companyRows.stream()
                .map(CompanyRow::getSymbol)
                .filter(symbol -> excludedTickers == null || !excludedTickers.contains(symbol))
                .collect(Collectors.joining(","));
    }

    private List<TipRanksStockDataDto> getDataByTicker(String url, String tickersStr) {
        String fullStockDataUrl = String.format("%s?tickers=%s", url, tickersStr);
        TipRanksDto tipRanksDto = get(fullStockDataUrl);
        return Optional.ofNullable(tipRanksDto)
                .orElseThrow(() -> new RuntimeException("Empty response"))
                .getData();
    }

    private TipRanksDto get(String url) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(TipRanksDto.class)
                .block();
    }

    @Override
    public void saveRows(Collection<TipRanksRow> rows) {
        tipRanksRepository.saveAll(rows);
    }
}
