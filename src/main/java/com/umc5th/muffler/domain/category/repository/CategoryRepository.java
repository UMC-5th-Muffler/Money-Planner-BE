package com.umc5th.muffler.domain.category.repository;

import com.umc5th.muffler.domain.category.repository.dto.NameProjection;
import com.umc5th.muffler.domain.category.repository.dto.OutlinedCategoryProjection;
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
public interface CategoryRepository extends JpaRepository<Category, Long>, CategoryRepositoryCustom {
    Optional<Category> findByIdAndStatus(Long id, Status status);
    @Query(value = "select c from Category c where c.member.id = :memberId and c.id = :categoryId AND c.status = 'ACTIVE'")
    Optional<Category> findCategoryWithCategoryIdAndMemberId(@Param("categoryId") Long categoryId, @Param("memberId") String memberId);

    @Query(value = "select c from Category c where c.member.id = :memberId and c.name = :name AND c.status = 'ACTIVE'")
    Optional<Category> findCategoryWithNameAndMemberId(@Param("name") String name, @Param("memberId") String memberId);
    @Query(value = "select c from Category c where c.id = :categoryId and c.status = 'ACTIVE'")
    Optional<Category> findActiveCategoryById(@Param("categoryId") Long categoryId);
    @Query("SELECT c FROM Category c WHERE c.status = 'ACTIVE' AND c.member.id = :memberId ORDER BY c.priority ASC")
    List<Category> findActiveCategoriesAsc(@Param(("memberId")) String memberId);
    @Query("SELECT c FROM Category c WHERE c.status = 'ACTIVE' AND c.member.id = :memberId")
    List<Category> findActiveCategories(@Param(("memberId")) String memberId);
    List<NameProjection> findByMemberAndStatus(Member member, Status status);
    List<OutlinedCategoryProjection> findByMemberAndIsVisibleAndStatusOrderByPriorityAsc(Member member, Boolean isVisible, Status status);
}
