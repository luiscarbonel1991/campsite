package com.reservation.campsite.persistence.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "availability")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false )
    private Integer id;
    @Column(name = "date", unique = true, columnDefinition = "DATE", nullable = false)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate date;
    @Column(name = "available", nullable = false)
    private Integer available = 0;

    @Column(name = "available_total", nullable = false)
    private Integer availableTotal = 0;

    @Version
    @Column(name = "version")
    private Integer version;

    public boolean hasAvailability() {
        return this.available > 0;
    }

    public boolean hasTotalAvailability() {
        return this.availableTotal > 0;
    }

}
