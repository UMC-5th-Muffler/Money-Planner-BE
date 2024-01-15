package com.umc5th.muffler.domain.routine.service;

import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.domain.routine.dto.AddWeeklyRoutineRequest;
import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.fixture.CategoryFixture;
import com.umc5th.muffler.fixture.MemberFixture;
import com.umc5th.muffler.global.response.exception.MemberException;
import com.umc5th.muffler.global.response.exception.RoutineException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;


@SpringBootTest
@Transactional
public class RoutineServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @InjectMocks
    private final RoutineService routineService;

    @Autowired
    public RoutineServiceTest(RoutineService routineService) {
        this.routineService = routineService;
    }


    @Test
    public void 등록되지_않은_멤버인_경우() throws RoutineException {
        // given
        Member member = MemberFixture.MEMBER_TWO;
        Category category = CategoryFixture.CATEGORY_ONE;
        AddWeeklyRoutineRequest request = new AddWeeklyRoutineRequest(1, 10000L, LocalDate.of(2024, 1, 13), null, "title", "memo", category.getId(), member.getId(), List.of(1, 2));

        given(memberRepository.findById(member.getId())).willReturn(Optional.empty());

        // then
        assertThrows(MemberException.class, () -> {
            routineService.addWeeklyRoutine(request);
        });
    }
}
