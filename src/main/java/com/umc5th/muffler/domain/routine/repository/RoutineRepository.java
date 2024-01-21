package com.umc5th.muffler.domain.routine.repository;

import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.Routine;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long> {

    Slice<Routine> findAllByMember(Member member, Pageable pageable);
}
