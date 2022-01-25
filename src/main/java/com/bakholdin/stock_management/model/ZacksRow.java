package com.bakholdin.stock_management.model;

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
    private Double price;
    @Column
    private Integer industryRank;
    @Column
    private Integer zacksRank;
    @Column(length = 1)
    private Character valueScore;
    @Column(length = 1)
    private Character growthScore;
    @Column(length = 1)
    private Character MomentumScore;
    @Column(length = 1)
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