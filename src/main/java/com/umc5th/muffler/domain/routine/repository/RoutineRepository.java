package com.umc5th.muffler.domain.routine.repository;

import com.umc5th.muffler.entity.Routine;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long> {
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Routine routine set routine.category.id = :etcCategoryId where routine.category.id = :deletedCategoryId")
    int updateRoutinesWithDeletedCategory(@Param("deletedCategoryId") Long deletedCategoryId, @Param("etcCategoryId") Long etcCategoryId);
}
