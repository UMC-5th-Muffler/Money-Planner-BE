package com.umc5th.muffler.domain.category.service;

import com.umc5th.muffler.domain.category.converter.CategoryConverter;
import com.umc5th.muffler.domain.category.dto.CategoryDto;
import com.umc5th.muffler.domain.category.dto.NewCategoryRequest;
import com.umc5th.muffler.domain.category.repository.CategoryRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.CategoryException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CategoryService {
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;

    public CategoryDto createNewCategory(Long memberId, NewCategoryRequest request) throws CategoryException {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CategoryException(ErrorCode.NOT_FOUND_MEMBER));
        Optional<Category> duplicatedCategory = categoryRepository.findCategoryWithNameAndMemberId(
                request.getCategoryName(), memberId);

        if (duplicatedCategory.isPresent())
            throw new CategoryException(ErrorCode.DUPLICATED_CATEGORY_NAME);
        Category newCategory = CategoryConverter.toEntity(request);
        member.addCategory(newCategory);
        newCategory = categoryRepository.save(newCategory);
        return new CategoryDto(newCategory.getId(), newCategory.getName());
    }
}
