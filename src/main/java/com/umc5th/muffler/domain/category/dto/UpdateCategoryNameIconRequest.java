package com.umc5th.muffler.domain.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "카테고리 아이콘/이름 수정 요청 객체")
public class UpdateCategoryNameIconRequest {
    @NotNull
    @Positive
    @Schema(description = "카테고리 아이디", example = "1")
    private Long categoryId;
    @NotBlank(message = "카테고리 이름은 공백이 될 수 없습니다.")
    @Schema(description = "수정한 카테고리 이름", example = "IT 기기")
    private String name;
    @NotBlank
    @Schema(description = "수정한 카테고리 아이콘", example = "아이콘1")
    private String icon;
}
