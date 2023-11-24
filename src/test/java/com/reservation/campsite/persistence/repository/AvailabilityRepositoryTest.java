package com.reservation.campsite.persistence.repository;

import com.reservation.campsite.persistence.entity.Availability;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@DirtiesContext
class AvailabilityRepositoryTest {

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Test
    @Transactional(value = Transactional.TxType.NEVER)
    void shouldThrowAnOptimisticLockingException() {
        var availability = Availability.builder()
                .date(LocalDate.now())
                .available(10)
                .availableTotal(10)
                .build();
        var entitySaved = availabilityRepository.save(availability);

        var firstEntity = availabilityRepository.findById(entitySaved.getId()).get();
        var firstEntity2 = availabilityRepository.findById(entitySaved.getId()).get();

        firstEntity.setAvailable(5);
        availabilityRepository.save(firstEntity);

        assertThatThrownBy(() -> {
            firstEntity2.setAvailable(3);
            availabilityRepository.save(firstEntity2);
        }).isInstanceOf(ObjectOptimisticLockingFailureException.class);

    }
}

