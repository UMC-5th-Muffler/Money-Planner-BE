package com.umc5th.muffler.domain.category.repository;

import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.constant.Status;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.umc5th.muffler.entity.Member;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByIdAndStatus(Long id, Status status);
    @Query(value = "select c from Category c where c.member.id = :memberId and c.id = :categoryId AND c.status = 'ACTIVE'")
    Optional<Category> findCategoryWithCategoryIdAndMemberId(@Param("categoryId") Long categoryId, @Param("memberId") String memberId);

    @Query(value = "select c from Category c where c.member.id = :memberId and c.name = :name AND c.status = 'ACTIVE'")
    Optional<Category> findCategoryWithNameAndMemberId(@Param("name") String name, @Param("memberId") String memberId);

    List<Category> findAllByMember(Member member);

    @Query("SELECT c FROM Category c WHERE c.member IS NULL")
    List<Category> findAllWithNoMember(); //공통 카테고리 찾기

    Long countByMember(Member member);
}
