package com.bakholdin.stock_management;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RestTemplateTestUtils {

    @SuppressWarnings("unchecked")
    public static <T> ResponseEntity<T> createMockResponseEntity(HttpStatus status, T body) {
        ResponseEntity<T> responseEntity = mock(ResponseEntity.class);
        when(responseEntity.getStatusCode()).thenReturn(status);
        when(responseEntity.getStatusCodeValue()).thenReturn(status.value());
        when(responseEntity.hasBody()).thenReturn(body != null && !body.toString().isBlank());
        when(responseEntity.getBody()).thenReturn(body);
        return responseEntity;
    }
}
