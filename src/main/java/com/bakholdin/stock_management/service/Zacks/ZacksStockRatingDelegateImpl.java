package com.bakholdin.stock_management.service.Zacks;

import com.bakholdin.stock_management.config.ApplicationProperties;
import com.bakholdin.stock_management.model.CompanyRow;
import com.bakholdin.stock_management.model.ZacksRow;
import com.bakholdin.stock_management.repository.CompanyRepository;
import com.bakholdin.stock_management.repository.ZacksRepository;
import com.bakholdin.stock_management.service.StockRatingDelegate;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ZacksStockRatingDelegateImpl implements StockRatingDelegate<ZacksRow> {
    private final RestTemplate restTemplate;
    private final ApplicationProperties applicationProperties;
    private final CompanyRepository companyRepository;
    private final ZacksRepository zacksRepository;

    @Override
    public List<ZacksRow> fetchRows() {
        String zacksCsvString = fetchCsvAsString();
        return parseCsvString(zacksCsvString);
    }

    @Override
    public void saveRows(Collection<ZacksRow> zacksRows) {
        Set<CompanyRow> companyRows = zacksRows.stream()
                .map(row -> row.getId().getCompanyRow())
                .collect(Collectors.toSet());
        companyRepository.saveAll(companyRows);
        zacksRepository.saveAll(zacksRows);
    }

    private List<ZacksRow> parseCsvString(String csvStr) {
        Reader reader = new StringReader(Objects.requireNonNull(csvStr));
        CsvParserSettings settings = new CsvParserSettings();
        BeanListProcessor<ZacksRow> rowProcessor = new BeanListProcessor<>(ZacksRow.class);
        settings.setProcessor(rowProcessor);
        CsvParser parser = new CsvParser(settings);
        parser.parse(reader);
        return rowProcessor.getBeans();
    }

    private String fetchCsvAsString() {
        HttpEntity<MultiValueMap<String, String>> httpEntity = createHttpEntity();
        ResponseEntity<String> csvResponse = restTemplate.exchange(
                "https://www.zacks.com/portfolios/tools/ajxExportExel.php",
                HttpMethod.POST,
                httpEntity,
                String.class
        );
        validateCsvResponse(csvResponse);
        return csvResponse.getBody();
    }

    private void validateCsvResponse(ResponseEntity<String> csvResponse) {
        String notOkMessage = String.format("Request to Zacks returned %d", csvResponse.getStatusCodeValue());
        Assert.isTrue(csvResponse.getStatusCode() == HttpStatus.OK, notOkMessage);
        Assert.hasText(csvResponse.getBody(), "Request to Zacks returned empty body");
    }

    private HttpEntity<MultiValueMap<String, String>> createHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.addAll(applicationProperties.getZacks().getHeaders());
        MultiValueMap<String, String> body = applicationProperties.getZacks().getBody();
        return new HttpEntity<>(body, headers);
    }
}
