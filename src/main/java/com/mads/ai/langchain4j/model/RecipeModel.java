package com.mads.ai.langchain4j.model;

import dev.langchain4j.model.output.structured.Description;

import java.util.List;

/**
 * 带限制描述的
 */
public class RecipeModel {
    @Description("short title, 3 words maximum")
    private String title;

    @Description("short description, 2 sentences maximum")
    private String description;

    @Description("each step should be described in 4 words, steps should rhyme")
    private List<String> steps;

    private Integer preparationTimeMinutes;

    @Override
    public String toString() {
        return "Recipe {" +
                " title = \"" + title + "\"" +
                ", description = \"" + description + "\"" +
                ", steps = " + steps +
                ", preparationTimeMinutes = " + preparationTimeMinutes +
                " }";
    }
}
