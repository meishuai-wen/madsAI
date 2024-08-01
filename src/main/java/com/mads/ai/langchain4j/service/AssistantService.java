package com.mads.ai.langchain4j.service;

import com.mads.ai.openai.model.OpenAIResponse;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

public interface AssistantService {

    String chat(@MemoryId int memoryId, @UserMessage String userMessage);

    String chatCalculator(String userMessage);

    AiMessage chat1(String userMessage);

    TokenStream chatStream(String message);


}
