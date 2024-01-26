package com.umc5th.muffler.entity;

import com.umc5th.muffler.entity.base.BaseTimeEntity;
import com.umc5th.muffler.entity.constant.CategoryType;
import com.umc5th.muffler.entity.constant.Status;
import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Builder
@Entity
@Getter
public class Category extends BaseTimeEntity {
    public static final String ETC_CATEGORY_NAME = "기타";
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 25, nullable = false)
    private String name;

    @Column(nullable = false)
    private String icon;

    @Column(nullable = false)
    private Long priority;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(15) DEFAULT 'ACTIVE'")
    private Status status;

    @Column(nullable = false)
    @ColumnDefault("true")
    private Boolean isVisible;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void setStatus(Status status) {
        this.status = status;
    }

    // 연관관계 메서드
    public void setMember(Member member) {
        this.member = member;
    }

    public Boolean isIconUpdatable(String newIcon) {
        return icon.equals(newIcon) || type == CategoryType.CUSTOM;
    }
    public Boolean isOwnMember(String memberId) {
        return member.getId().equals(memberId);
    }
    public Boolean isNameUpdatable(String newName) {
        return name.equals(newName) || !name.equals(ETC_CATEGORY_NAME);
    }

    // 생성 메서드
    public static Category defaultCategory(String name, String icon, Long priority) {
        return Category.builder()
                .name(name)
                .icon(icon)
                .priority(priority)
                .isVisible(true)
                .type(CategoryType.DEFAULT)
                .status(Status.ACTIVE)
                .build();
    }
}
