package com.mads.ai.model;

import lombok.Data;

@Data
public class OpenAiMessage {
    /**
     * 取值
     *  user/system/tool(函数)/assistant（询问）
     */
    private String role;
    private Object content;
    private String name;
}
