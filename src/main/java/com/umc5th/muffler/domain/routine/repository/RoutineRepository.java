package com.umc5th.muffler.domain.routine.repository;

import com.umc5th.muffler.entity.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long>, RoutineRepositoryCustom {

    @Query("SELECT r FROM Routine r JOIN FETCH r.category WHERE r.id = :routineId AND r.member.id = :memberId")
    Optional<Routine> findByIdAndMemberIdWithCategory(Long routineId, String memberId);

    Optional<Routine> findByIdAndMemberId(Long routineId, String memberId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Routine routine set routine.category.id = :etcCategoryId where routine.category.id = :deletedCategoryId")
    int updateRoutinesWithDeletedCategory(@Param("deletedCategoryId") Long deletedCategoryId, @Param("etcCategoryId") Long etcCategoryId);
}
