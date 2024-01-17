package com.umc5th.muffler.domain.category.repository;

import com.umc5th.muffler.entity.Category;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.umc5th.muffler.entity.Member;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query(value = "select c from Category c where c.member.id = :memberId and c.name = :name")
    Optional<Category> findCategoryWithNameAndMemberId(@Param("name") String name, @Param("memberId") Long memberId);
    
    List<Category> findAllByMember(Member member);
}
