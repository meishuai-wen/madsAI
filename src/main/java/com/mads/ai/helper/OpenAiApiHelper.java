package com.mads.ai.helper;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.mads.ai.model.OpenAIRequest;
import com.mads.ai.model.OpenAIResponse;
import com.mads.ai.model.OpenAiMediaMessage;
import com.mads.ai.model.OpenAiMessage;
import com.mads.ai.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

/**
 * api方式
 */
@Slf4j
@Component
public class OpenAiApiHelper {

    private static final String key = "sk-None-C7CEIyIXGH3hHdz3X5P8T3BlbkFJsnnaFyLlnFXlYBaQXamg";
    private static final String DOMAIN = "https://api.openai.com/v1";
    private static final String CHAT_BASE_URL = DOMAIN + "/chat";
    private static final String MODELS_BASE_URL = DOMAIN + "/models";

    public static Optional<OpenAiMessage> sendMessage(String model, List<OpenAiMessage> messages) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(CHAT_BASE_URL + "/completions");

        OpenAIRequest request = new OpenAIRequest();
        request.setMessages(messages);
        request.setModel(model);

        try {

            String res = HttpRequest.post(builder.toUriString())
                    .header(Header.AUTHORIZATION, "Bearer " + key)
                    .header(Header.CONTENT_TYPE, "application/json")
                    .body(JsonUtil.toJson(request).orElse(""))
                    .timeout(30000)//超时，毫秒
                    .execute()
                    .body();

            log.info("openAi sendMessage res:{}", res);

            OpenAIResponse ret = JsonUtil.toObject(res, OpenAIResponse.class).orElse(null);

            if (Objects.isNull(ret)) {
                log.error("[OpenAI] request error");
                return Optional.empty();
            }
            OpenAIResponse.Choice[] choices = ret.getChoices();
            if (choices.length == 0) {
                log.error("[OpenAI] choices.length == 0");
                return Optional.empty();
            }
            return Optional.of(choices[0].getMessage());
        } catch (Exception ex) {
            log.warn("[OpenAI] send message failed", ex);
            return Optional.empty();
        }
    }

    public static String getModels() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(MODELS_BASE_URL);
        return HttpRequest.get(builder.toUriString())
                .header(Header.AUTHORIZATION, "Bearer " + key)
                .header(Header.CONTENT_TYPE, "application/json")
                .timeout(30000)//超时，毫秒
                .execute().body();
    }

    /**
     * 很基础的文本
     */
    public void testDefaultMessage() {
        List<OpenAiMessage> messages = new ArrayList<>();
        OpenAiMessage currentMessage = new OpenAiMessage();
        currentMessage.setRole("system");
        currentMessage.setContent("You are a poetic assistant, skilled in explaining complex programming concepts with creative flair.");
        messages.add(currentMessage);

        OpenAiMessage currentMessage1 = new OpenAiMessage();
        currentMessage1.setRole("user");
        currentMessage1.setContent("Compose a poem that explains the concept of recursion in programming.");
        messages.add(currentMessage1);

        Optional<OpenAiMessage> message = sendMessage("gpt-3.5-turbo", messages);
        System.out.println(JsonUtil.toJson(message).get());
    }

    /***
     * 多组合的内容
     */
    public void testMediaMessage() {
        List<OpenAiMessage> messages = new ArrayList<>();

        OpenAiMessage currentMessage1 = new OpenAiMessage();
        currentMessage1.setRole("user");

        List<Object> list = new ArrayList<>();
        OpenAiMediaMessage.MediaTextMessage mediaTextMessage = new OpenAiMediaMessage.MediaTextMessage();
        mediaTextMessage.setText("what are in this image");
        list.add(mediaTextMessage);

        OpenAiMediaMessage.MediaImageMessage mediaImageMessage = new OpenAiMediaMessage.MediaImageMessage();
        mediaImageMessage.setImageUrl(Collections.singletonList( OpenAiMediaMessage.MediaImageModel.builder().url("xx.jpg").detail("low").build()));
        list.add(mediaImageMessage);


        currentMessage1.setContent(list);
        messages.add(currentMessage1);

        Optional<OpenAiMessage> message = sendMessage("gpt-4o", messages);
        System.out.println(JsonUtil.toJson(message).get());
    }


}
