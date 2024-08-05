package com.mads.ai.langchain4j.tool;

import com.mads.ai.util.DateTimeUtil;
import dev.langchain4j.agent.tool.Tool;

import java.time.Instant;
import java.time.LocalDate;

public class DateTool {

    @Tool("Get the current date and time")
    public static String nowTime() {
        System.out.println("获取时间");
        return DateTimeUtil.InstantToDateToString(Instant.now());
    }

    @Tool("Calculate the specific date after the specified number of days")
    public static String date(Integer days) {
        return LocalDate.now().plusDays(days).toString();
    }
}
