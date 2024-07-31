package com.mads.ai.openai.function;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获取天气的
 */
@Data
public class OpenAiWeatherFunction {
    @JsonPropertyDescription("City and state, for example: León, Guanajuato")
    public String location;

    @JsonPropertyDescription("The temperature unit, can be 'celsius' or 'fahrenheit'")
    @JsonProperty(required = true)
    public WeatherUnit unit;

    public enum WeatherUnit {
        CELSIUS, //摄氏度
        FAHRENHEIT;//华氏度
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class WeatherResponse {
        public String location;
        public WeatherUnit unit;
        public int temperature;
        public String description;
    }
}
