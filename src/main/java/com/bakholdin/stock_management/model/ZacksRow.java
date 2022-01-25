package com.bakholdin.stock_management.model;

import com.bakholdin.stock_management.model.csv_converters.CsvZacksColumn;
import com.bakholdin.stock_management.model.csv_converters.CsvZacksDouble;
import com.univocity.parsers.annotations.Nested;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "zacks")
public class ZacksRow {
    @EmbeddedId
    @Nested
    private StockManagementRowId id;
    @Column
    @CsvZacksDouble(field = "Price")
    private Double price;
    @Column
    @CsvZacksColumn(field = "Industry Rank")
    private Integer industryRank;
    @Column
    @CsvZacksColumn(field = "Zacks Rank")
    private Integer zacksRank;
    @Column(length = 1)
    @CsvZacksColumn(field = "Value Score")
    private Character valueScore;
    @Column(length = 1)
    @CsvZacksColumn(field = "Growth Score")
    private Character growthScore;
    @Column(length = 1)
    @CsvZacksColumn(field = "Momentum Score")
    private Character MomentumScore;
    @Column(length = 1)
    @CsvZacksColumn(field = "VGM Score")
    private Character vgmScore;

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