package com.bakholdin.stock_management.config.properties;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SendGridProperties {
    @NonNull private List<String> errorEmailList;
    @NonNull private List<String> genericEmailList;
}
