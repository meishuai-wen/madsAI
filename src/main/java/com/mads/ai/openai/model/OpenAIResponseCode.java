//package com.mads.ai.model;
//
//import com.fasterxml.jackson.annotation.JsonCreator;
//import com.fasterxml.jackson.annotation.JsonValue;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//public enum OpenAIResponseCode {
//    UNKNOW(""),
//    insufficient_quota("insufficient_quota")//没有配额了
//    ;
//
//    private String code;
//
//    OpenAIResponseCode(String code) {
//        this.code = code;
//    }
//
//    @JsonCreator
//    public static OpenAIResponseCode of(String code) {
//        for (OpenAIResponseCode appType : values()) {
//            if (appType.code.equals(code)) {
//                return appType;
//            }
//        }
//        log.warn("openAi UNKNOW code:{}", code);
//        return UNKNOW;
//    }
//
//
//    @JsonValue
//    public String getCode() {
//        return code;
//    }
//}
