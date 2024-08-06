package com.mads.ai.spring;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.stream.Collectors;

/***
 * 未来可期吧，
 */
@Component
public class SpringAITest {

    @Autowired
    private ChatClient chatClient;


    /***
     * 简答的文字问答
     */
    public void test1() {
        String q = "";
        ChatClient.CallResponseSpec response = chatClient.prompt()
                .user(q)
                .call();
        System.out.println(response);
    }

    @PostConstruct
    public void testStream() {
        String q = "Why did the pirate go to the comedy club? To hear some arrr-rated jokes! Arrr, matey";
        Flux<String> flux = chatClient.prompt()
                .system("system prompt ")
                .user(q)
                .stream()
                .content();
        String collect = flux.collectList().block().stream().collect(Collectors.joining());

        System.out.println(collect);
    }



}
