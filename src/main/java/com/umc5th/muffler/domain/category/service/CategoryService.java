package com.umc5th.muffler.domain.category.service;

import com.umc5th.muffler.domain.category.dto.CategoryConverter;
import com.umc5th.muffler.domain.category.dto.CategoryDto;
import com.umc5th.muffler.domain.category.dto.NewCategoryRequest;
import com.umc5th.muffler.domain.category.dto.UpdateCategoryRequest;
import com.umc5th.muffler.domain.category.repository.CategoryRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.constant.Status;
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

    public CategoryDto createNewCategory(String memberId, NewCategoryRequest request) throws CategoryException {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CategoryException(ErrorCode.MEMBER_NOT_FOUND));
        Optional<Category> duplicatedCategory = categoryRepository.findCategoryWithNameAndMemberId(
                request.getCategoryName(), memberId);
        Category newCategory;

        if (duplicatedCategory.isPresent()) {
            throw new CategoryException(ErrorCode.DUPLICATED_CATEGORY_NAME);
        }
        newCategory = CategoryConverter.toEntity(request);
        member.addCategory(newCategory);
        newCategory = categoryRepository.save(newCategory);
        return new CategoryDto(newCategory.getId(), newCategory.getName());
    }

    public void renameCategory(String memberId, UpdateCategoryRequest request) throws CategoryException {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CategoryException(ErrorCode.MEMBER_NOT_FOUND));
        Category originalCategory = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryException(ErrorCode.CATEGORY_NOT_FOUND));

        if (!originalCategory.isOwnMember(memberId))
            throw new CategoryException(ErrorCode.ACCESS_TO_OTHER_USER_CATEGORY);
        Optional<Category> duplicatedCategory = categoryRepository.findCategoryWithNameAndMemberId(request.getName(), memberId);
        Category updatedCategory;

        if (duplicatedCategory.isPresent())
            throw new CategoryException(ErrorCode.DUPLICATED_CATEGORY_NAME);
        if (!originalCategory.isIconUpdatable(request.getIcon()))
            throw new CategoryException(ErrorCode.CANNOT_UPDATE_DEFAULT_ICON);
        updatedCategory = CategoryConverter.toEntity(originalCategory, request);
        categoryRepository.save(updatedCategory);
    }
}
