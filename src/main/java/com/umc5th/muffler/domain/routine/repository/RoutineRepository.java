package com.umc5th.muffler.domain.routine.repository;

import com.umc5th.muffler.entity.Routine;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long>, RoutineRepositoryCustom {

    @EntityGraph(attributePaths = {"category"})
    Slice<Routine> findAllByMemberId(String memberId, Pageable pageable);

    @Query("SELECT r FROM Routine r JOIN FETCH r.category WHERE r.id = :routineId")
    Optional<Routine> findByIdAndCategory(Long routineId);

}
