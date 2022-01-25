package com.bakholdin.stock_management.service;

import com.bakholdin.stock_management.config.ApplicationProperties;
import com.bakholdin.stock_management.model.ZacksRow;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ZacksStockRatingDelegateImpl implements StockRatingDelegate<ZacksRow> {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ApplicationProperties applicationProperties;

    @Override
    public Set<ZacksRow> fetchRows() {
        HttpHeaders headers = new HttpHeaders();
        headers.addAll(applicationProperties.getZacks().getHeaders());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //headers.setContentLength(48); //TODO: make this dynamic
        MultiValueMap<String, String> body = applicationProperties.getZacks().getBody();
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> csvStr = restTemplate.exchange(
                "https://www.zacks.com/portfolios/tools/ajxExportExel.php",
                HttpMethod.POST,
                httpEntity,
                String.class
        );
        if(!csvStr.hasBody() || csvStr.getStatusCode() != HttpStatus.OK)
            throw new NotImplementedException("Return was not what expected");

        Reader reader = new StringReader(Objects.requireNonNull(csvStr.getBody()));
        CsvToBean<ZacksRow> csvReader = new CsvToBeanBuilder<ZacksRow>(reader)
                .withSeparator(',')
                .withIgnoreLeadingWhiteSpace(true)
                .withIgnoreEmptyLine(true)
                .withType(ZacksRow.class)
                //.withMappingStrategy()
                .build();

        return new HashSet<>(csvReader.parse());
    }
}