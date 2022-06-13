package com.bakholdin.stock_management.mapper;

import com.bakholdin.stock_management.dto.TipRanksStockDataDto;
import com.bakholdin.stock_management.model.TipRanksRow;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TipRanksMapper {

    @Mapping(target = "id.companyRow.symbol", source = "ticker")
    @Mapping(target = "analystConsensus", source = "analystConsensus.consensus")
    @Mapping(target = "bestAnalystConsensus", source = "bestAnalystConsensus.consensus")
    TipRanksRow fromDto(TipRanksStockDataDto tipRankDto);

}
