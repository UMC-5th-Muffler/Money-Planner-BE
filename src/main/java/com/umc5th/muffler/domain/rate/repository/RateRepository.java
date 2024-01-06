package com.umc5th.muffler.domain.rate.repository;

import com.umc5th.muffler.entity.Rate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RateRepository extends JpaRepository<Rate, Long> {
}
