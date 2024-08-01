package com.mads.ai.langchain4j.tool;

import com.mads.ai.util.DateTimeUtil;
import dev.langchain4j.agent.tool.Tool;

import java.time.Instant;

public class DateTool {

    @Tool("Get the current date and time")
    public static String nowTime() {
        System.out.println("获取时间");
        return DateTimeUtil.InstantToDateToString(Instant.now());
    }
}
