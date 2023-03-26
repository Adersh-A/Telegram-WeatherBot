package com.macro.tgbot.dto;

import lombok.Data;

@Data
public class GeoCode {
    private String name;
    private double lat;
    private double lon;
    private String country;
    private String state;
}
