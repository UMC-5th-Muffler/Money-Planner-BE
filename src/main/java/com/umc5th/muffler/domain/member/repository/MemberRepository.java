package com.umc5th.muffler.domain.member.repository;

import com.umc5th.muffler.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
