package com.umc5th.muffler.domain.routine.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class FindInsertableRoutineTest {
    private final JDBCRoutineRepository jdbcRoutineRepository;

    @Autowired
    FindInsertableRoutineTest(JDBCRoutineRepository jdbcRoutineRepository) {
        this.jdbcRoutineRepository = jdbcRoutineRepository;
    }

    @Test
    @Transactional
    void 조회_가능_여부() {
        this.jdbcRoutineRepository.findInsertableRoutines(LocalDate.now());
    }
}