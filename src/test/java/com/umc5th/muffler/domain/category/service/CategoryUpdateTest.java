package com.umc5th.muffler.domain.category.service;

import static com.umc5th.muffler.global.response.code.ErrorCode.CANNOT_UPDATE_DEFAULT_ICON;
import static com.umc5th.muffler.global.response.code.ErrorCode.MEMBER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.umc5th.muffler.domain.category.dto.UpdateCategoryNameIconRequest;
import com.umc5th.muffler.domain.category.repository.CategoryRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.fixture.CategoryFixture;
import com.umc5th.muffler.fixture.MemberFixture;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.CategoryException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
public class CategoryUpdateTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private MemberRepository memberRepository;
    @InjectMocks
    private CategoryService categoryService;

    @Test
    @Transactional
    void 커스텀카테고리_수정_성공() {
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.CUSTOM_CATEGORY_ONE;
        UpdateCategoryNameIconRequest request = new UpdateCategoryNameIconRequest(category.getId(),
                "수정이름", "수정 아이콘");
        member.addCategory(category);

        given(memberRepository.findById(any(String.class))).willReturn(Optional.of(member));
        given(categoryRepository.findCategoryWithCategoryIdAndMemberId(any(Long.class), any(String.class))).willReturn(Optional.of(category));
        given(categoryRepository.findCategoryWithNameAndMemberId(any(String.class), any(String.class)))
                .willReturn(Optional.empty());

        categoryService.updateNameOrIcon(member.getId(), request);

        assertThat(category.getId()).isEqualTo(request.getCategoryId());
        assertThat(category.getIcon()).isEqualTo(request.getIcon());
        assertThat(category.getName()).isEqualTo(request.getName());
    }

    @Test
    @Transactional
    void 디폴트카테고리_아이콘_외_수정_성공() {
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.DEFAULT_CATEGORY_FOUR;
        UpdateCategoryNameIconRequest request = new UpdateCategoryNameIconRequest(category.getId(),
                "수정이름", category.getIcon());
        member.addCategory(category);

        given(memberRepository.findById(any(String.class))).willReturn(Optional.of(member));
        given(categoryRepository.findCategoryWithCategoryIdAndMemberId(any(Long.class), any(String.class))).willReturn(Optional.of(category));
        given(categoryRepository.findCategoryWithNameAndMemberId(any(String.class), any(String.class)))
                .willReturn(Optional.empty());

        categoryService.updateNameOrIcon(member.getId(), request);

        assertThat(category.getId()).isEqualTo(request.getCategoryId());
        assertThat(category.getIcon()).isEqualTo(request.getIcon());
        assertThat(category.getName()).isEqualTo(request.getName());
    }

    @Test
    @Transactional
    void 디폴트카테고리_아이콘_수정_실패() {
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.DEFAULT_CATEGORY_FOUR;
        UpdateCategoryNameIconRequest request = new UpdateCategoryNameIconRequest(category.getId(),
                "수정이름", "수정아이콘");
        member.addCategory(category);

        given(memberRepository.findById(any(String.class))).willReturn(Optional.of(member));
        given(categoryRepository.findCategoryWithCategoryIdAndMemberId(any(Long.class), any(String.class))).willReturn(Optional.of(category));

        assertThatThrownBy(() ->categoryService.updateNameOrIcon(member.getId(), request))
                .isInstanceOf(CategoryException.class)
                .hasFieldOrPropertyWithValue("errorCode",CANNOT_UPDATE_DEFAULT_ICON);
    }


    @Test
    @Transactional
    void 사용자아이디가_존재하지_않는_경우_실패() {
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.DEFAULT_CATEGORY_FOUR;
        UpdateCategoryNameIconRequest request = new UpdateCategoryNameIconRequest(category.getId(),
                "수정이름", "수정아이콘");
        member.addCategory(category);

        given(memberRepository.findById(any(String.class))).willReturn(Optional.empty());

        assertThatThrownBy(() ->categoryService.updateNameOrIcon(member.getId(), request))
                .isInstanceOf(CategoryException.class)
                .hasFieldOrPropertyWithValue("errorCode",MEMBER_NOT_FOUND);
    }

    @Test
    @Transactional
    void 활성화된_카테고리가_존재하지_않는_경우_실패() {
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.DEFAULT_CATEGORY_FOUR;
        UpdateCategoryNameIconRequest request = new UpdateCategoryNameIconRequest(category.getId(),
                "수정이름", "수정아이콘");
        member.addCategory(category);

        given(memberRepository.findById(any(String.class))).willReturn(Optional.of(member));
        given(categoryRepository.findCategoryWithCategoryIdAndMemberId(any(Long.class), any(String.class))).willReturn(Optional.empty());

        assertThatThrownBy(() ->categoryService.updateNameOrIcon(member.getId(), request))
                .isInstanceOf(CategoryException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CATEGORY_NOT_FOUND);
    }

    @Test
    @Transactional
    void 다른사람의_카테고리를_수정하려는_경우_실패() {
        Member member = MemberFixture.MEMBER_ONE;
        Member other = MemberFixture.MEMBER_TWO;
        Category category = CategoryFixture.DEFAULT_CATEGORY_FOUR;
        UpdateCategoryNameIconRequest request = new UpdateCategoryNameIconRequest(category.getId(),
                "수정이름", "수정아이콘");
        other.addCategory(category);

        given(memberRepository.findById(any(String.class))).willReturn(Optional.of(member));
        given(categoryRepository.findCategoryWithCategoryIdAndMemberId(any(Long.class), any(String.class))).willReturn(Optional.of(category));

        assertThatThrownBy(() ->categoryService.updateNameOrIcon(member.getId(), request))
                .isInstanceOf(CategoryException.class)
                .hasFieldOrPropertyWithValue("errorCode",ErrorCode.CATEGORY_NOT_FOUND);
    }

    @Test
    @Transactional
    void 활성화된_같은_이름의_카테고리가_존재하는_경우_실패() {
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.DEFAULT_CATEGORY_FOUR;
        Category sameNamed = CategoryFixture.createSameNamedDifferentCategory(category);
        UpdateCategoryNameIconRequest request = new UpdateCategoryNameIconRequest(category.getId(),
                "수정이름", category.getIcon());
        member.addCategory(category);

        given(memberRepository.findById(any(String.class))).willReturn(Optional.of(member));
        given(categoryRepository.findCategoryWithCategoryIdAndMemberId(any(Long.class), any(String.class))).willReturn(Optional.of(category));
        given(categoryRepository.findCategoryWithNameAndMemberId(any(String.class), any(String.class)))
                .willReturn(Optional.of(sameNamed));

        assertThatThrownBy(() ->categoryService.updateNameOrIcon(member.getId(), request))
                .isInstanceOf(CategoryException.class)
                .hasFieldOrPropertyWithValue("errorCode",ErrorCode.DUPLICATED_CATEGORY_NAME);
    }

    @Test
    @Transactional
    void 기타_카테고리의_이름을_수정하려는_경우_실패() {
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.ETC_CATEGORY;
        UpdateCategoryNameIconRequest request = new UpdateCategoryNameIconRequest(category.getId(),
                "수정이름", category.getIcon());
        member.addCategory(category);

        given(memberRepository.findById(any(String.class))).willReturn(Optional.of(member));
        given(categoryRepository.findCategoryWithCategoryIdAndMemberId(any(Long.class), any(String.class))).willReturn(Optional.of(category));

        assertThatThrownBy(() ->categoryService.updateNameOrIcon(member.getId(), request))
                .isInstanceOf(CategoryException.class)
                .hasFieldOrPropertyWithValue("errorCode",ErrorCode.CANNOT_UPDATE_ETC_CATEGORY_NAME);
    }
}
