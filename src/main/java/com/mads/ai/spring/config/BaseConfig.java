package com.mads.ai.spring.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BaseConfig {

    @Bean
    public ChatClient initChatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("You are a friendly chat bot that answers question in the voice of a Pirate")
                //记忆上下文
//                .defaultAdvisors(new PromptChatMemoryAdvisor(chatMemory),
//                        // new MessageChatMemoryAdvisor(chatMemory), // CHAT MEMORY
//                        new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults()),
//                        new LoggingAdvisor())
                .build();
    }


}
