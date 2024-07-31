package com.mads.ai.openai.model;

import lombok.Data;

@Data
public class OpenAIResponse {
    private ResponseError error;

    private String id;

    private Choice[] choices;

    @Data
    public static class Choice {
        private OpenAiMessage message;
        /**
         * 每个响应将包含一个finish_reason。 的可能值为finish_reason：
         *
         * stop：API 返回完整消息，或者由stop参数提供的停止序列之一终止的消息
         * length：由于max_tokens参数或 token 限制导致模型输出不完整
         * function_call：模型决定调用一个函数
         * content_filter：由于我们的内容过滤器的标记，省略了内容
         * null：API 响应仍在进行中或未完成
         */
        private String finishReason;
        private int index;
    }

    /***
     *  错误码：https://platform.openai.com/docs/guides/error-codes/api-errors
     */
    @Data
    public static class ResponseError {
        private String type;
        private String code;//错误码 insufficient_quota：超出配额
        private String message;//错误信息描述
        private Object param;
    }
}
