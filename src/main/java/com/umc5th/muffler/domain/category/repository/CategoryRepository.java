package com.umc5th.muffler.domain.category.repository;

import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByMember(Member member);

    @Query("SELECT c FROM Category c WHERE c.member IS NULL")
    List<Category> findAllWithNoMember(); //공통 카테고리 찾기
}
