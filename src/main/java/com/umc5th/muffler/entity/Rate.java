package com.umc5th.muffler.entity;

import com.umc5th.muffler.entity.base.BaseTimeEntity;
import com.umc5th.muffler.entity.constant.Level;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Getter
public class Rate extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1024)
    private String memo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private Level totalLevel;

    @OneToMany(mappedBy =  "rate", cascade = CascadeType.ALL)
    private List<CategoryRate> categoryRates;

    public void setCategoryRates(List<CategoryRate> categoryRates){
        this.categoryRates = categoryRates;
        categoryRates.forEach(categoryRate -> categoryRate.setRate(this));
    }

    public void addCategoryRate(CategoryRate categoryRate){
        this.categoryRates.add(categoryRate);
        categoryRate.setRate(this);
    }


    public void update(String memo, Level totalLevel){
        this.memo = memo;
        this.totalLevel = totalLevel;
    }
}
