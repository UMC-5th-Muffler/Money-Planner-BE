package com.umc5th.muffler.domain.category.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.umc5th.muffler.domain.category.dto.NewCategoryResponse;
import com.umc5th.muffler.domain.category.dto.NewCategoryRequest;
import com.umc5th.muffler.domain.category.repository.CategoryRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.Member;

import com.umc5th.muffler.fixture.CategoryFixture;
import com.umc5th.muffler.fixture.MemberFixture;
import com.umc5th.muffler.global.response.exception.CategoryException;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private MemberRepository memberRepository;
    @InjectMocks
    private CategoryService categoryService;
    @Captor
    private ArgumentCaptor<Category> categoryArgumentCaptor;

    @Test
    public void 정상실행() throws CategoryException {
        //given
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.CUSTOM_CATEGORY_ONE;
        NewCategoryRequest request = new NewCategoryRequest(category.getName(), category.getIcon(), 0L);

        given(categoryRepository.findCategoryWithNameAndMemberId(request.getName(), member.getId()))
                .willReturn(Optional.empty());
        given(memberRepository.findById(member.getId()))
                .willReturn(Optional.ofNullable(member));
        given(categoryRepository.save(any(Category.class))).willReturn(category);

        // when
        NewCategoryResponse newCategory = categoryService.createNewCategory("1", request);
        //then
        verify(categoryRepository).save(categoryArgumentCaptor.capture());

        Category saved = categoryArgumentCaptor.getValue();
        Assertions.assertEquals(request.getName(), saved.getName());
    }

    @Test
    public void 중복_이름() throws CategoryException {
        // given
        Member member = MemberFixture.MEMBER_ONE;
        Category haveCategory = CategoryFixture.CUSTOM_CATEGORY_ONE;
        member.addCategory(haveCategory);

        NewCategoryRequest request = new NewCategoryRequest(haveCategory.getName(), haveCategory.getIcon(), 0L);
        // when
        given(memberRepository.findById(member.getId())).willReturn(Optional.ofNullable(member));
        given(categoryRepository.findCategoryWithNameAndMemberId(request.getName(), member.getId()))
                .willReturn(Optional.of(haveCategory));

        // then
        Assertions.assertThrows(CategoryException.class, () -> categoryService.createNewCategory(member.getId(), request));
    }

    @Test
    @Transactional
    public void 사용자_아이디가_없는경우() throws CategoryException {
        //given
        Member member = MemberFixture.MEMBER_TWO;
        Category category = CategoryFixture.CUSTOM_CATEGORY_ONE;
        NewCategoryRequest request = new NewCategoryRequest(category.getName(), category.getIcon(), 0L);

        given(memberRepository.findById(member.getId())).willReturn(Optional.empty());
        // when
        Assertions.assertThrows(CategoryException.class, () -> categoryService.createNewCategory(member.getId(), request));
    }
}