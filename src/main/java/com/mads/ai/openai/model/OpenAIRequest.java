package com.mads.ai.openai.model;

import lombok.Data;

import java.util.List;

@Data
public class OpenAIRequest {
    private String model = "gpt-3.5-turbo";
    private List<OpenAiMessage> messages;

    private boolean stream;//是否开启流传输
    private String seed = "seed";//可以告诉大模型生成一致的输出，在比较重复的内容是有作用


    private float temperature = 1f;
    private int maxTokens = 400;
}
