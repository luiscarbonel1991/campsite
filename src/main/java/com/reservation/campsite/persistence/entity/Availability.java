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

    @Column(name = "available_total", nullable = false)
    private Integer availableTotal = 0;

    public boolean hasAvailability() {
        return this.available > 0;
    }

    public boolean hasTotalAvailability() {
        return this.availableTotal > 0;
    }

}
