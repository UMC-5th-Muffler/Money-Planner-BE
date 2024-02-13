package com.umc5th.muffler.entity;

import static com.umc5th.muffler.entity.constant.Status.ACTIVE;

import com.umc5th.muffler.entity.base.BaseTimeEntity;
import com.umc5th.muffler.entity.constant.Role;
import com.umc5th.muffler.entity.constant.SocialType;
import com.umc5th.muffler.entity.constant.Status;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Builder
@Entity
@Getter
@Table(name = "members")
public class Member extends BaseTimeEntity implements Persistable<String>, UserDetails {

    @Id
    private String id;

    @Column(length = 20)
    private String name;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Enumerated(EnumType.STRING)
    private Role role; // authority는 하나만 가능 (ex. "USER,ADMIN"처럼 2개 불가능)

    @Column
    private String refreshToken;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Status status = ACTIVE;

    @OneToMany(mappedBy = "member")
    private List<Goal> goals;
  
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Category> categories = new ArrayList<>();

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private MemberAlarm memberAlarm;

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void addGoal(Goal goal) {
        this.goals.add(goal);
    }

    public void removeGoal(Goal goal) {
        this.goals.remove(goal);
    }

    public void addCategory(Category category) {
        category.setMember(this);
        this.categories.add(category);
    }
    public void setMemberAlarm(MemberAlarm memberAlarm) {
        memberAlarm.setMember(this);
        this.memberAlarm = memberAlarm;
    }
    public void enrollToken(String token) {
        memberAlarm.enrollToken(token);
    }
    @Override
    public boolean isNew() {
        return getCreatedAt() == null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role);
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return status.isActive();
    }

    @Override
    public boolean isAccountNonLocked() {
        return status.isActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return status.isActive();
    }

    @Override
    public boolean isEnabled() {
        return status.isActive();
    }
}
