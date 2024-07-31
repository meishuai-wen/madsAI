package com.mads.ai.langchain4j.service;

import com.mads.ai.langchain4j.model.RecipeModel;
import com.mads.ai.langchain4j.prompt.CreateRecipePrompt;

public interface ChefService {
    RecipeModel createRecipeFrom(String... ingredients);

    RecipeModel createRecipe(CreateRecipePrompt prompt);
}
