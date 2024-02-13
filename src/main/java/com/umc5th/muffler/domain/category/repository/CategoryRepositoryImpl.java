package com.umc5th.muffler.domain.category.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc5th.muffler.entity.QCategory;
import com.umc5th.muffler.entity.constant.Status;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existsDuplicateName(String memberId, Status status, String name) {
        QCategory category = QCategory.category;

        return queryFactory.select(category.name)
                .from(category)
                .where(category.member.id.eq(memberId)
                        .and(category.status.eq(status))
                        .and(category.name.eq(name))
                ).fetchFirst() != null;
    }
}
