package com.umc5th.muffler.domain.member.repository;

import com.umc5th.muffler.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findByRefreshToken(String refreshToken);
}
