package com.bakholdin.stock_management.model;

import com.bakholdin.stock_management.model.csv_parsers.CsvCharacterConverter;
import com.bakholdin.stock_management.model.csv_parsers.CsvDoubleConverter;
import com.bakholdin.stock_management.model.csv_parsers.CsvIntegerConverter;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.MappingStrategy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "zacks")
public class ZacksRow {
    @EmbeddedId
    private StockManagementRowId id;
    @Column
    @CsvCustomBindByName(column = "Price", converter = CsvDoubleConverter.class)
    private Double price;
    @Column
    @CsvCustomBindByName(column = "Industry Rank", converter = CsvIntegerConverter.class)
    private Integer industryRank;
    @Column
    @CsvCustomBindByName(column = "Zacks Rank", converter = CsvIntegerConverter.class)
    private Integer zacksRank;
    @Column(length = 1)
    @CsvCustomBindByName(column = "Value Score", converter = CsvCharacterConverter.class)
    private Character valueScore;
    @Column(length = 1)
    @CsvCustomBindByName(column = "Growth Score", converter = CsvCharacterConverter.class)
    private Character growthScore;
    @Column(length = 1)
    @CsvCustomBindByName(column = "Momentum Score", converter = CsvCharacterConverter.class)
    private Character MomentumScore;
    @Column(length = 1)
    @CsvCustomBindByName(column = "VGM Score", converter = CsvCharacterConverter.class)
    private Character vgmScore;

    public MappingStrategy<StockManagementRowId> getMappingStrategy() {
        MappingStrategy<StockManagementRowId> mappingStrategy = new HeaderColumnNameMappingStrategy<>();

        return mappingStrategy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ZacksRow zacksRow = (ZacksRow) o;
        return id != null && Objects.equals(id, zacksRow.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}