package com.mads.ai.langchain4j.service;

import com.mads.ai.langchain4j.enums.Sentiment;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 翻译小助手
 */
public interface TextTranslatorService {
    //用户翻译
    @SystemMessage("You are a professional translator into {{language}}")
    @UserMessage("Translate the following text: {{text}}")
    String translate(@V("text") String text, @V("language") String language);

    //从文件里拿到提示词
    @SystemMessage(fromResource = "/translator-system-prompt-template.txt")
    @UserMessage(fromResource = "/translator-user-prompt-template.txt")
    String translateFromFile(@V("text") String text, @V("language") String language);

    //总结用户的每条消息，值提供要点
    @SystemMessage("Summarize every message from user in {{n}} bullet points. Provide only bullet points.")
    List<String> summarize(@UserMessage String text, @V("n") int n);

    //情感分析
    @UserMessage("Analyze sentiment of {{it}}")
    Sentiment analyzeSentimentOf(String text);

    //情感分析
    @UserMessage("Does {{it}} have a positive sentiment?")
    boolean isPositive(String text);

    //文本数据类型转换
    @UserMessage("Extract number from {{it}}")
    int extractInt(String text);

    @UserMessage("Extract number from {{it}}")
    long extractLong(String text);

    @UserMessage("Extract number from {{it}}")
    BigInteger extractBigInteger(String text);

    @UserMessage("Extract number from {{it}}")
    float extractFloat(String text);

    @UserMessage("Extract number from {{it}}")
    double extractDouble(String text);

    @UserMessage("Extract number from {{it}}")
    BigDecimal extractBigDecimal(String text);

    @UserMessage("Extract date from {{it}}")
    LocalDate extractDateFrom(String text);

    @UserMessage("Extract time from {{it}}")
    LocalTime extractTimeFrom(String text);

    @UserMessage("Extract date and time from {{it}}")
    LocalDateTime extractDateTimeFrom(String text);

}
