package com.umc5th.muffler.entity;

import com.umc5th.muffler.entity.base.BaseTimeEntity;
import java.time.LocalDate;
import javax.persistence.*;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Getter
@Table(indexes = {@Index(name = "expense_index", columnList = "date")})
public class Expense extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String title;

    @Column(nullable = true)
    private String memo;

    @Column(nullable = false)
    private Long cost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
  

    public static Expense of(LocalDate date, String title, String memo, Long cost, Member member, Category category) {
        return Expense.builder()
                .date(date)
                .title(title)
                .memo(memo)
                .cost(cost)
                .member(member)
                .category(category)
                .build();
    }

    public void setTitleAndMemo(String title, String memo) {
        this.title = title;
        this.memo = memo;
    }
    public void setCost(Long cost) {
        this.cost = cost;
    }
    public void setMember(Member member) {
        this.member = member;
    }
    public void setCategory(Category category) {
        this.category = category;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public boolean isOwnMember(String memberId) { return member.getId().equals(memberId);}
    public boolean isCategoryChanged(Long inputId) {
        return !category.getId().equals(inputId);
    }
    public boolean isDateChanged(LocalDate inputDate) {
        return !date.isEqual(inputDate);
    }

    public boolean isCostChanged(Long expenseCost) {
        return !this.cost.equals(expenseCost);
    }
}
