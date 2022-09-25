package com.a205.mafya.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@EqualsAndHashCode(callSuper=false)
public class RefMonth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "month", nullable = false)
    private int month;

    @Column(name = "day", nullable = false)
    private int day;
}
