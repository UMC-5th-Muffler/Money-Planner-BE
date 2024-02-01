package com.umc5th.muffler.domain.category.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.umc5th.muffler.domain.category.repository.CategoryRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.domain.routine.repository.RoutineRepository;
import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.constant.Status;
import com.umc5th.muffler.fixture.CategoryFixture;
import com.umc5th.muffler.fixture.MemberFixture;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.CategoryException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
public class CategoryDeleteTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private RoutineRepository routineRepository;
    @Captor
    private ArgumentCaptor<Category> categoryCaptor;
    @InjectMocks
    private CategoryService categoryService;

    @Test
    @Transactional
    void 커스텀_카테고리_삭제_성공() {
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.CUSTOM_CATEGORY_ONE;
        Category etcCategory = CategoryFixture.ETC_CATEGORY;
        member.addCategory(category);
        member.addCategory(etcCategory);

        given(memberRepository.findById(any(String.class))).willReturn(Optional.of(member));
        given(categoryRepository.findActiveCategoryById(any(Long.class))).willReturn(Optional.of(category));
        given(categoryRepository.findCategoryWithNameAndMemberId(any(String.class), any(String.class)))
                .willReturn(Optional.of(etcCategory));
        given(routineRepository.updateRoutinesWithDeletedCategory(any(Long.class), any(Long.class))).willReturn(1);

        categoryService.deactivateCategory(member.getId(), category.getId());

        verify(categoryRepository).save(categoryCaptor.capture());

        Category captorCategory = categoryCaptor.getValue();
        assertEquals(captorCategory.getStatus(), Status.INACTIVE);
    }

    @Test
    @Transactional
    void 사용자가_없는_경우_삭제_실패() {
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.CUSTOM_CATEGORY_ONE;
        Category etcCategory = CategoryFixture.ETC_CATEGORY;
        member.addCategory(category);
        member.addCategory(etcCategory);

        given(memberRepository.findById(any(String.class))).willReturn(Optional.empty());

        assertThatThrownBy(() ->categoryService.deactivateCategory(member.getId(), category.getId()))
                .isInstanceOf(CategoryException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND);
    }

    @Test
    @Transactional
    void 카테고리가_없는_경우_삭제_실패() {
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.CUSTOM_CATEGORY_ONE;
        Category etcCategory = CategoryFixture.ETC_CATEGORY;
        member.addCategory(category);
        member.addCategory(etcCategory);

        given(memberRepository.findById(any(String.class))).willReturn(Optional.of(member));
        given(categoryRepository.findActiveCategoryById(any(Long.class))).willReturn(Optional.empty());

        assertThatThrownBy(() ->categoryService.deactivateCategory(member.getId(), category.getId()))
                .isInstanceOf(CategoryException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CATEGORY_NOT_FOUND);
    }

    @Test
    @Transactional
    void 다른_사용자의_카테고리_삭제_실패() {
        Member member = MemberFixture.MEMBER_ONE;
        Member other = MemberFixture.MEMBER_TWO;
        Category category = CategoryFixture.CUSTOM_CATEGORY_ONE;
        Category etcCategory = CategoryFixture.ETC_CATEGORY;
        other.addCategory(category);
        member.addCategory(etcCategory);

        given(memberRepository.findById(any(String.class))).willReturn(Optional.of(member));
        given(categoryRepository.findActiveCategoryById(any(Long.class))).willReturn(Optional.of(category));

        assertThatThrownBy(() ->categoryService.deactivateCategory(member.getId(), category.getId()))
                .isInstanceOf(CategoryException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_TO_OTHER_USER_CATEGORY);
    }

    @Test
    @Transactional
    void 이미_삭제된_카테고리의_경우_삭제_실패() {
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.INACTIVE_CATEGORY_SIX;
        Category etcCategory = CategoryFixture.ETC_CATEGORY;
        member.addCategory(category);
        member.addCategory(etcCategory);

        given(memberRepository.findById(any(String.class))).willReturn(Optional.of(member));
        given(categoryRepository.findActiveCategoryById(any(Long.class))).willReturn(Optional.of(category));

        assertThatThrownBy(() ->categoryService.deactivateCategory(member.getId(), category.getId()))
                .isInstanceOf(CategoryException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALREADY_INACTIVE_CATEGORY);
    }

    @Test
    @Transactional
    void 기타_카테고리가_삭제된_경우_삭제_실패() {
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.ETC_CATEGORY;
        Category etcCategory = CategoryFixture.ETC_CATEGORY;
        member.addCategory(category);
        member.addCategory(etcCategory);

        given(memberRepository.findById(any(String.class))).willReturn(Optional.of(member));
        given(categoryRepository.findActiveCategoryById(any(Long.class))).willReturn(Optional.of(category));

        assertThatThrownBy(() ->categoryService.deactivateCategory(member.getId(), category.getId()))
                .isInstanceOf(CategoryException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CANNOT_DELETE_DEFAULT_CATEGORY);
    }

    @Test
    @Transactional
    void 디폴트_카테고리_삭제_실패() {
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.DEFAULT_CATEGORY_FOUR;
        Category etcCategory = CategoryFixture.ETC_CATEGORY;
        member.addCategory(category);
        member.addCategory(etcCategory);

        given(memberRepository.findById(any(String.class))).willReturn(Optional.of(member));
        given(categoryRepository.findActiveCategoryById(any(Long.class))).willReturn(Optional.of(category));

        assertThatThrownBy(() ->categoryService.deactivateCategory(member.getId(), category.getId()))
                .isInstanceOf(CategoryException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CANNOT_DELETE_DEFAULT_CATEGORY);
    }
}
