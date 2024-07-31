package com.mads.ai.langchain4j.service;

import com.mads.ai.langchain4j.model.PersonModel;
import dev.langchain4j.service.UserMessage;

/**
 * 对象转换器，模型将从输入中提取有用信息，封装成Person对象
 */
public interface PersonExtractorService {
    @UserMessage("Extract information about a person from {{it}}")
    PersonModel extractPersonFrom(String text);
}
