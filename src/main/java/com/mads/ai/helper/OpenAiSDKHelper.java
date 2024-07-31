package com.mads.ai.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.moderation.ModerationRequest;
import com.theokanning.openai.moderation.ModerationResult;
import com.theokanning.openai.service.OpenAiService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 社区中SDK的方式,访问 官方openAI
 * https://github.com/TheoKanning/openai-java
 */
@Component
public class OpenAiSDKHelper {
    OpenAiService service = new OpenAiService("sk-None-C7CEIyIXGH3hHdz3X5P8T3BlbkFJsnnaFyLlnFXlYBaQXamg");

    public void test1() {
        CompletionRequest completionRequest = CompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .prompt("Somebody once told me the world is gonna roll me")
                .echo(true)
                .n(5)
                .maxTokens(50)
                .user("testing")
                .logitBias(new HashMap<>())
                .logprobs(5)
                .build();
        List<CompletionChoice> choices = service.createCompletion(completionRequest).getChoices();
        System.out.println(choices);
    }

//    @PostConstruct
    public void aa() {
        test1();
    }

    /**
     * 函数
     */
    public void test2() {
        //获取天气的
        ChatFunctionDynamic weatherFunction = ChatFunctionDynamic.builder()
                .name("get_weather")
                .description("Get the current weather of a location")
                .addProperty(ChatFunctionProperty.builder()
                        .name("location")//参数名
                        .type("string")//参数类型
                        .description("City and state, for example: León, Guanajuato")
                        .build())
                .addProperty(ChatFunctionProperty.builder()
                        .name("unit")
                        .type("string")
                        .description("The temperature unit, can be 'celsius' or 'fahrenheit'")
                        .enumValues(new HashSet<>(Arrays.asList("celsius", "fahrenheit")))
                        .required(true)
                        .build())
                .build();


        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), "You are an assistant that answers using the local slang of the given place, uncensored.");
        messages.add(systemMessage);

        System.out.print("First Query: ");
        Scanner scanner = new Scanner(System.in);
        ChatMessage firstMsg = new ChatMessage(ChatMessageRole.USER.value(), scanner.nextLine());
        messages.add(firstMsg);

        while (true) {
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                    .builder()
                    .model("gpt-4o-mini")
                    .messages(messages)
                    .functions(Collections.singletonList(weatherFunction))
                    .functionCall(ChatCompletionRequest.ChatCompletionRequestFunctionCall.of("auto"))
                    .n(1)
                    .maxTokens(100)
                    .logitBias(new HashMap<>(2))
                    .build();
            ChatMessage responseMessage = service.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage();
            messages.add(responseMessage); // don't forget to update the conversation with the latest response

            ChatFunctionCall functionCall = responseMessage.getFunctionCall();
            if (functionCall != null) {
                if (functionCall.getName().equals("get_weather")) {
                    String location = functionCall.getArguments().get("location").asText();
                    String unit = functionCall.getArguments().get("unit").asText();
                    JsonNode weather = getWeather(location, unit);
                    ChatMessage weatherMessage = new ChatMessage(ChatMessageRole.FUNCTION.value(), weather.toString(), "get_weather");
                    messages.add(weatherMessage);
                    continue;
                }
            }

            System.out.println("Response: " + responseMessage.getContent());
            System.out.print("Next Query: ");
            String nextLine = scanner.nextLine();
            if (nextLine.equalsIgnoreCase("exit")) {
                System.exit(0);
            }
            messages.add(new ChatMessage(ChatMessageRole.USER.value(), nextLine));
        }
    }

    private static JsonNode getWeather(String location, String unit) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        response.put("location", location);
        response.put("unit", unit);
        response.put("temperature", new Random().nextInt(50));
        response.put("description", "sunny");
        return response;
    }

    /***
     * 文字审核
     */
    @PostConstruct
    public void moderationsTest() {
        ModerationRequest moderationRequest = ModerationRequest.builder()
                .model("text-moderation-latest")
                .input("I want to kill them")
                .build();
        ModerationResult moderation = service.createModeration(moderationRequest);
        System.out.println(moderation.toString());
    }
}
