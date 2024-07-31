package com.mads.ai.langchain4j.model;

import dev.langchain4j.model.input.structured.StructuredPrompt;
import lombok.Data;

import java.util.List;

@Data
//创建食谱的数据，这个注解和在ChefService中使用@UserMessage应该是一样的效果
@StructuredPrompt("Create a recipe of a {{dish}} that can be prepared using only {{ingredients}}")
public class CreateRecipePrompt {
    private String dish;
    private List<String> ingredients;
}
