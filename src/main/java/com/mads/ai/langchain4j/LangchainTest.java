package com.mads.ai.langchain4j;

import com.mads.ai.langchain4j.config.ApiKeys;
import com.mads.ai.langchain4j.service.AssistantService;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;

public class LangchainTest {
    ChatLanguageModel chatLanguageModel = OpenAiChatModel.builder()
            .apiKey(ApiKeys.OPENAI_API_KEY)
//            .modelName("gpt-4o-mini")
            .build();

    //聊天 -单次，没有下文记录
    public void chat() {
        String answer = chatLanguageModel.generate("message");
        System.out.println(answer);
    }

    //多轮聊天，是有上下文的（不带存储）
    public void chatMore() {
        //用户的消息
        SystemMessage systemMessage = SystemMessage.systemMessage("你是一个问答小助手");
        UserMessage userMessage1 = UserMessage.userMessage("你好，我是Mads");
        Response<AiMessage> response1 = chatLanguageModel.generate(userMessage1);
        //AI返回的消息
        AiMessage aiMessage1 = response1.content();
        System.out.println(aiMessage1.text());

        //todo 猜测是函数相关，先不关注
//        ToolExecutionResultMessage toolExecutionResultMessage = ToolExecutionResultMessage.toolExecutionResultMessage("","","");

        Response<AiMessage> response2 = chatLanguageModel.generate(systemMessage, userMessage1, aiMessage1, UserMessage.userMessage("我叫什么"));
        AiMessage aiMessage2 = response2.content();
        System.out.println(aiMessage2.text());
    }

    ///聊天 流式（需要有正式的apiKey），内容将是分批满满返回的
    public void chatStream() {
        StreamingChatLanguageModel model = OpenAiStreamingChatModel.builder()
                .apiKey("demo")
                .build();

        model.generate("你好，你是谁？", new StreamingResponseHandler<AiMessage>() {
            @Override
            public void onNext(String token) {
                System.out.println(token);
            }

            @Override
            public void onError(Throwable error) {
                System.out.println(error);
            }
        });
    }

    public void chatStream1() {
        StreamingChatLanguageModel model = OpenAiStreamingChatModel.builder()
                .apiKey("demo")
                .build();
        AssistantService assistant = AiServices.create(AssistantService.class, model);

        TokenStream tokenStream = assistant.chatStream("Tell me a joke");

        tokenStream.onNext(System.out::println)
                .onComplete(System.out::println)
                .onError(Throwable::printStackTrace)
                .start();
    }

    public static void main(String[] args) {
        new LangchainTest().chatStream();
    }
}
