package com.bakholdin.stock_management.config;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Getter
@Setter
public class ZacksProperties {
    @NonNull private String holdingsUrl;
    @NonNull private MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    @NonNull private MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
}
