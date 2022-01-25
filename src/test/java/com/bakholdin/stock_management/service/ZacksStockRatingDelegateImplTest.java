package com.bakholdin.stock_management.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
class ZacksStockRatingDelegateImplTest {

    private final RestTemplate restTemplate = mock(RestTemplate.class);
    private final ZacksStockRatingDelegateImpl zacksStockRatingDelegate = mock(ZacksStockRatingDelegateImpl.class);

    @Test
    void fetchRows() {

    }
}