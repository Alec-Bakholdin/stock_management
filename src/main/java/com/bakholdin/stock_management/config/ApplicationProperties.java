package com.bakholdin.stock_management.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties("stock-management")
public class ApplicationProperties {
    @NestedConfigurationProperty
    private ZacksProperties zacks;
    @NestedConfigurationProperty
    private TipRanksProperties tipRanks;
    @NestedConfigurationProperty
    private YahooProperties yahoo;
}
