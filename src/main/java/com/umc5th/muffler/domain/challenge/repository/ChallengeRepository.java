package com.umc5th.muffler.domain.challenge.repository;

import com.umc5th.muffler.entity.ChallengeGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeRepository extends JpaRepository<ChallengeGroup, Long> {
}
