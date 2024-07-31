package com.mads.ai.openai.service;

import com.theokanning.openai.assistants.Assistant;
import com.theokanning.openai.assistants.AssistantRequest;
import com.theokanning.openai.messages.Message;
import com.theokanning.openai.messages.MessageRequest;
import com.theokanning.openai.runs.Run;
import com.theokanning.openai.runs.RunCreateRequest;
import com.theokanning.openai.threads.Thread;
import com.theokanning.openai.threads.ThreadRequest;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * beta版本的
 * 文档：https://platform.openai.com/docs/assistants/quickstart?lang=curl
 */
public interface OpenAiApiV2 {

    //创建助手
    @Headers({"OpenAI-Beta: assistants=v2"})
    @POST("/v1/assistants")
    Single<Assistant> createAssistant(@Body AssistantRequest request);

    //创建会话
    @Headers({"OpenAI-Beta: assistants=v2"})
    @POST("/v1/threads")
    Single<Thread> createThread(@Body ThreadRequest request);

    //会话发消息
    @Headers({"OpenAI-Beta: assistants=v2"})
    @POST("/v1/threads/{thread_id}/messages")
    Single<Message> createMessage(@Path("thread_id") String threadId, @Body MessageRequest request);

    //创建运行
    @Headers("OpenAI-Beta: assistants=v2")
    @POST("/v1/threads/{thread_id}/runs")
    Single<Run> createRun(@Path("thread_id") String threadId, @Body RunCreateRequest runCreateRequest);
}
