package com.mads.ai.helper;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.*;
import com.azure.core.credential.KeyCredential;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * openAI和微软维护的 SDK的方式，此SDK只能访问微软维护的openAI
 * https://github.com/Azure/azure-sdk-for-java/tree/main/sdk/openai/azure-ai-openai
 */
@Component
public class OpenAiAzureSDKHelper {
    OpenAIClient client = new OpenAIClientBuilder()
            .credential(new KeyCredential("{openai-secret-key}"))
            .buildClient();

    //创建使用代理的客户端
//    private OpenAIClient getProxyClient() {
//        String hostname = "{your-host-name}";
//        int port = 447;
//
//        ProxyOptions proxyOptions = new ProxyOptions(ProxyOptions.Type.HTTP, new InetSocketAddress(hostname, port))
//                .setCredentials("{username}", "{password}");
//
//        return new OpenAIClientBuilder()
//                .credential(new AzureKeyCredential("{key}"))
//                .endpoint("{endpoint}")
//                .clientOptions(new HttpClientOptions().setProxyOptions(proxyOptions))
//                .buildClient();
//    }

    public void test1() {
        List<ChatRequestMessage> chatMessages = new ArrayList<>();
        chatMessages.add(new ChatRequestSystemMessage("You are a helpful assistant. You will talk like a pirate."));
        chatMessages.add(new ChatRequestUserMessage("Can you help me?"));
        chatMessages.add(new ChatRequestAssistantMessage("Of course, me hearty! What can I do for ye?"));
        chatMessages.add(new ChatRequestUserMessage("What's the best way to train a parrot?"));

        ChatCompletions chatCompletions = client.getChatCompletions("{deploymentOrModelName}",
                new ChatCompletionsOptions(chatMessages));

        System.out.printf("Model ID=%s is created at %s.%n", chatCompletions.getId(), chatCompletions.getCreatedAt());
        for (ChatChoice choice : chatCompletions.getChoices()) {
            ChatResponseMessage message = choice.getMessage();
            System.out.printf("Index: %d, Chat Role: %s.%n", choice.getIndex(), message.getRole());
            System.out.println("Message:");
            System.out.println(message.getContent());
        }
    }

}
