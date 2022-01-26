package com.bakholdin.stock_management.service.TipRanks;

import com.bakholdin.stock_management.repository.CompanyRepository;
import com.bakholdin.stock_management.repository.TipRanksRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(SpringExtension.class)
class TipRanksStockRatingDelegateImplTest {

    @Spy
    @InjectMocks
    private TipRanksStockRatingDelegateImpl tipRanksStockRatingDelegate;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private TipRanksRepository tipRanksRepository;
}