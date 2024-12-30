package com.dayone.persist.entity;

import com.dayone.model.Dividend;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity(name = "DIVIDEND")
@Getter
@NoArgsConstructor
@ToString
public class DividendEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long comanyId;

    private LocalDateTime date;

    private String dividend;

    public DividendEntity(Long comanyId, Dividend dividend) {
        this.comanyId = comanyId;
        this.date = dividend.getDate();
        this.dividend = dividend.getDividend();
    }
}
