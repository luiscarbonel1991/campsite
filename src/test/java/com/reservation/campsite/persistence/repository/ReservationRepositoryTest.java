package com.reservation.campsite.persistence.repository;

import com.reservation.campsite.persistence.entity.Reservation;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Instant;
import java.time.LocalDate;

@DataJpaTest
@DirtiesContext
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    @Transactional(value = Transactional.TxType.NEVER)
    void shouldThrowAnOptimisticLockingException() {
        Reservation reservation = Reservation.builder()
                .name("Optimistic")
                .email("optimistic@test.com")
                .arrivalDate(LocalDate.now())
                .departureDate(LocalDate.now().plusDays(1))
                .createdDate(Instant.now())
                .updateDate(Instant.now())
                .cancelDate(Instant.now())
                .build();

        var entitySaved = reservationRepository.save(reservation);

        var firstEntity = reservationRepository.findById(entitySaved.getId()).get();
        var firstEntity2 = reservationRepository.findById(entitySaved.getId()).get();

        firstEntity.setEmail("optimistic1@test.com");
        reservationRepository.save(firstEntity);

        Assertions.assertThatThrownBy(() -> {
            firstEntity2.setEmail("optimistic2@test.com");
            reservationRepository.save(firstEntity2);
        }).isInstanceOf(ObjectOptimisticLockingFailureException.class);

    }
}

