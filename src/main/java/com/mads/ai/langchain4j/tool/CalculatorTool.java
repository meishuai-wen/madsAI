package com.mads.ai.langchain4j.tool;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

/**
 * 计算器,AI可以根据工具名字自定选择调用
 */
public class CalculatorTool {

    //统计字符长度
    @Tool("Calculates the length of a string")
    public static int stringLength(@P("user content") String s) {
        System.out.println("Called stringLength() with s='" + s + "'");
        return s.length();
    }

    //计算两数之和
    @Tool("Calculates the sum of two numbers")
    public static int add(int a, int b) {
        System.out.println("Called add() with a=" + a + ", b=" + b);
        return a + b;
    }

    //计算两数乘积
    @Tool("Calculates the product of two numbers")
    public static int product(int a, int b) {
        System.out.println("Called product() with a=" + a + ", b=" + b);
        return a * b;
    }

    //计算平方根
    @Tool("Calculates the square root of a number")
    public static double sqrt(int x) {
        System.out.println("Called sqrt() with x=" + x);
        return Math.sqrt(x);
    }
}
