package com.macro.tgbot.dto;

import lombok.Data;

@Data
public class WeatherDetails {

    private String place;
    private String state;
    private String country;
    private String weather;
    private String aqi;
}
