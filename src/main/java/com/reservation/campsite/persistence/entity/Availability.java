package com.reservation.campsite.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "availability")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false )
    private Integer id;
    @Column(name = "date", unique = true, columnDefinition = "DATE", nullable = false)
    private LocalDate date;
    @Column(name = "available", nullable = false)
    private Integer available = 0;

}
