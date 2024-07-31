package com.mads.ai.langchain4j.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

public interface AssistantService {

    String chat(@MemoryId int memoryId, @UserMessage String userMessage);

    String chatCalculator(String userMessage);

    TokenStream chatStream(String message);


}
