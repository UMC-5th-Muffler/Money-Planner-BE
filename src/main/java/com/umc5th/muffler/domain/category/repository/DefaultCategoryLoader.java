package com.umc5th.muffler.domain.category.repository;

import static net.minidev.json.parser.JSONParser.MODE_JSON_SIMPLE;

import com.umc5th.muffler.domain.category.dto.DefaultCategoryDTO;
import com.umc5th.muffler.entity.Category;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class DefaultCategoryLoader {
    private final List<Category> defaultCategories;

    public DefaultCategoryLoader() throws IOException, ParseException {
        JSONParser parser = new JSONParser(MODE_JSON_SIMPLE);
        ClassPathResource resource = new ClassPathResource("defaultCategoryList.json");
        BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
        JSONArray jsonArray = (JSONArray) parser.parse(reader);
        long priority = 1;

        defaultCategories = new ArrayList<>();
        for (Object object : jsonArray) {
            JSONObject jsonObject = (JSONObject) object;
            defaultCategories.add(toCategory(jsonObject, priority));
            priority += 1;
        }
    }

    public List<DefaultCategoryDTO> getDefaultCategoryDTOS(String memberId) {
        return defaultCategories.stream().map(category -> DefaultCategoryDTO.builder()
                .name(category.getName())
                .icon(category.getIcon())
                .priority(category.getPriority())
                .type(category.getType().toString())
                .isVisible(category.getIsVisible())
                .status(category.getStatus().toString())
                .memberId(memberId)
                .build()
        ).collect(Collectors.toList());
    }

    public int getTotalDefaultCategorySize() { return this.defaultCategories.size();}

    private Category toCategory(JSONObject jsonCategoryObject, long priority) {
        String categoryName = (String)jsonCategoryObject.get("name");
        String categoryIcon = (String)jsonCategoryObject.get("icon");
        return Category.defaultCategory(categoryName, categoryIcon, priority);
    }
}
