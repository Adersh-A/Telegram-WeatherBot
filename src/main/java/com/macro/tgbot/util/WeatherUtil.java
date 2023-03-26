package com.macro.tgbot.util;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class WeatherUtil {

    public String getAqiDescription(String key) {
        Map<String, String> aqiMap = new HashMap<>();
        aqiMap.put("1", "Good");
        aqiMap.put("2", "Fair");
        aqiMap.put("3", "Moderate");
        aqiMap.put("4", "Poor");
        aqiMap.put("5", "Very Poor");
        return aqiMap.get(key);
    }

}
