package com.mads.ai.langchain4j;

import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 提示词 模板
 */
public class PromptTest {

    //一个占位的提示提格式化
    public void promptWithOneVariable() {
        PromptTemplate promptTemplate = PromptTemplate.from("Say 'hi' in {{it}}.");

        Prompt prompt = promptTemplate.apply("German");

        System.out.println(prompt.text()); // Say 'hi' in German.
    }

    //多占位的提示提格式化
    public void promptWithMoreVariable() {
        PromptTemplate promptTemplate = PromptTemplate.from("Say '{{text}}' in {{language}}.");

        Map<String, Object> variables = new HashMap<>();
        variables.put("text", "hi");
        variables.put("language", "German");

        Prompt prompt = promptTemplate.apply(variables);

        System.out.println(prompt.text()); // Say 'hi' in German.
    }

    public static void main(String[] args) {
        new PromptTest().promptWithMoreVariable();
    }
}
