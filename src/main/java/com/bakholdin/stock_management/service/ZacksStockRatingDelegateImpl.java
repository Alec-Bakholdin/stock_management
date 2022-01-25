package com.bakholdin.stock_management.service;

import com.bakholdin.stock_management.config.ApplicationProperties;
import com.bakholdin.stock_management.model.ZacksRow;
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
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ZacksStockRatingDelegateImpl implements StockRatingDelegate<ZacksRow> {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ApplicationProperties applicationProperties;

    @Override
    public List<ZacksRow> fetchRows() {
        String zacksCsvString = fetchCsvAsString();
        return parseCsvString(zacksCsvString);
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
