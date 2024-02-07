package com.umc5th.muffler.domain.category.repository;

import com.umc5th.muffler.entity.constant.Status;

public interface CategoryRepositoryCustom {
    boolean existsDuplicateName(String memberId, Status status, String name);
}
