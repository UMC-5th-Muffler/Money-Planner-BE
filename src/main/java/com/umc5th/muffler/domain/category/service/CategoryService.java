package com.umc5th.muffler.domain.category.service;

import com.umc5th.muffler.domain.category.dto.CategoryConverter;
import com.umc5th.muffler.domain.category.dto.CategoryDTO;
import com.umc5th.muffler.domain.category.dto.CategoryFilterOptions;
import com.umc5th.muffler.domain.category.dto.GetCategoryListResponse;
import com.umc5th.muffler.domain.category.dto.NewCategoryResponse;
import com.umc5th.muffler.domain.category.dto.DeleteCategoryResponse;
import com.umc5th.muffler.domain.category.dto.NewCategoryRequest;
import com.umc5th.muffler.domain.category.dto.UpdateCategoryNameIconRequest;
import com.umc5th.muffler.domain.category.dto.CategoryFilterUpdateRequest;
import com.umc5th.muffler.domain.category.repository.BatchUpdateCategoryRepository;
import com.umc5th.muffler.domain.category.repository.CategoryRepository;
import com.umc5th.muffler.domain.category.repository.dto.NameProjection;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.domain.routine.repository.RoutineRepository;
import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.constant.CategoryType;
import com.umc5th.muffler.entity.constant.Status;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.CategoryException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
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
    private final RoutineRepository routineRepository;
    private final CategoryRepository categoryRepository;
    private final BatchUpdateCategoryRepository batchUpdateCategoryRepository;

    public NewCategoryResponse createNewCategory(String memberId, NewCategoryRequest request) throws CategoryException {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CategoryException(ErrorCode.MEMBER_NOT_FOUND));
        List<NameProjection> categoryNames = categoryRepository.findByMemberAndStatus(member, Status.ACTIVE);
        Optional<NameProjection> duplicateName = categoryNames.stream()
                .filter(row -> row.getName().equals(request.getName()))
                .findAny();
        if (duplicateName.isPresent()) {
            throw new CategoryException(ErrorCode.DUPLICATED_CATEGORY_NAME);
        }
        Category newCategory = CategoryConverter.toEntity(request, categoryNames.size() + 1L, member);
        newCategory = categoryRepository.save(newCategory);
        return CategoryConverter.toDTO(newCategory);
    }

    public void updateNameOrIcon(String memberId, UpdateCategoryNameIconRequest request) throws CategoryException {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CategoryException(ErrorCode.MEMBER_NOT_FOUND));
        Category category = categoryRepository.findCategoryWithCategoryIdAndMemberId(request.getCategoryId(), memberId)
                .orElseThrow(() -> new CategoryException(ErrorCode.CATEGORY_NOT_FOUND));

        if (category.isNameChanged(request.getName())) {
            if (!category.isNameUpdatable()) {
                throw new CategoryException(ErrorCode.CANNOT_UPDATE_ETC_CATEGORY_NAME);
            }
            if (categoryRepository
                    .existsDuplicateName(memberId, Status.ACTIVE, request.getName())) {
                throw new CategoryException(ErrorCode.DUPLICATED_CATEGORY_NAME);
            }
            category.changeName(request.getName());
        }
        if (category.isIconChanged(request.getIcon())) {
            if (!category.isIconUpdatable(request.getIcon())) {
                throw new CategoryException(ErrorCode.CANNOT_UPDATE_DEFAULT_ICON);
            }
            category.changeIcon(request.getIcon());
        }
    }

    public void updateBatchPriorityOrVisibility(String memberId, CategoryFilterUpdateRequest request)
            throws CategoryException {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CategoryException(ErrorCode.MEMBER_NOT_FOUND));
        Map<Long, Category> entityMap = categoryRepository.findActiveCategories(memberId)
                .stream().collect(Collectors.toMap(Category::getId, item -> item));
        List<CategoryFilterOptions> requestCategories = request.getCategories();
        List<CategoryFilterOptions> updateList = new ArrayList<>();
        long expectOrder = 1L;

        requestCategories.sort(Comparator.comparingLong(CategoryFilterOptions::getPriority));
        for (CategoryFilterOptions requestCategory : requestCategories) {
            if (!requestCategory.getPriority().equals(expectOrder++)) {
                throw new CategoryException(ErrorCode.CATEGORY_UNEXPECTED_ORDER);
            }
            Category category = entityMap.get(requestCategory.getCategoryId());
            if (category == null) {
                throw new CategoryException(ErrorCode.CATEGORY_NOT_FOUND);
            }
            if (isChanged(category, requestCategory)) {
                updateList.add(requestCategory);
            }
        }
        if (!updateList.isEmpty()) {
            batchUpdateCategoryRepository.batchUpdatePriorityAndVisibility(updateList);
        }
    }

    private Boolean isChanged(Category category, CategoryFilterOptions dto) {
        return category.getIsVisible() != dto.getIsVisible() || !category.getPriority().equals(dto.getPriority());
    }

    public DeleteCategoryResponse deactivateCategory(String memberId, Long categoryId) throws CategoryException {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CategoryException(ErrorCode.MEMBER_NOT_FOUND));
        Category category = categoryRepository.findCategoryWithCategoryIdAndMemberId(categoryId, memberId)
                .orElseThrow(() -> new CategoryException(ErrorCode.CATEGORY_NOT_FOUND));

        if (category.getType() == CategoryType.DEFAULT) {
            throw new CategoryException(ErrorCode.CANNOT_DELETE_DEFAULT_CATEGORY);
        }
        category.setStatus(Status.INACTIVE);
        categoryRepository.save(category);

        Category etcCategory = categoryRepository.findCategoryWithNameAndMemberId(Category.ETC_CATEGORY_NAME, memberId)
                .orElseThrow(() -> new CategoryException(ErrorCode.ETC_CATEGORY_NOT_FOUND));
        int updatedRows = routineRepository.updateRoutinesWithDeletedCategory(categoryId, etcCategory.getId());
        return new DeleteCategoryResponse(updatedRows);
    }

    @Transactional(readOnly = true)
    public GetCategoryListResponse getActiveCategories(String memberId) throws CategoryException {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CategoryException(ErrorCode.MEMBER_NOT_FOUND));
        List<CategoryDTO> categories = categoryRepository.findActiveCategoriesAsc(member.getId())
                .stream().map(CategoryConverter::toFullCategoryDTO)
                .collect(Collectors.toList());
        return new GetCategoryListResponse(categories);
    }

    @Transactional(readOnly = true)
    public GetCategoryListResponse getVisibleOutlineCategories(String memberId) throws CategoryException {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CategoryException(ErrorCode.MEMBER_NOT_FOUND));
        List<CategoryDTO> outlineCategoryDTOs = categoryRepository
                .findByMemberAndIsVisibleAndStatusOrderByPriorityAsc(member, true, Status.ACTIVE)
                .stream().map(CategoryConverter::toOutlineCategoryDTO)
                .collect(Collectors.toList());
        return new GetCategoryListResponse(outlineCategoryDTOs);
    }
}
