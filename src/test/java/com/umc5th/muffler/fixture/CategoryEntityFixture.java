package com.umc5th.muffler.fixture;

import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.constant.Status;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CategoryEntityFixture {

    public static List<Category> createList(int num){
        return IntStream.rangeClosed(1, num)
                .mapToObj(i -> Category.builder()
                .name("name")
                .status(Status.ACTIVE)
                .icon("icon")
                .build())
                .collect(Collectors.toList());
    }
}
