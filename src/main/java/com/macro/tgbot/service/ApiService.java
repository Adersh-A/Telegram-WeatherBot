package com.macro.tgbot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.macro.tgbot.dto.GeoCode;
import com.macro.tgbot.dto.WeatherDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.List;

@Service
@Slf4j
public class ApiService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    ObjectMapper objectMapper;
    @Value("${api.key}")
    private String apiKey;


    public WeatherDetails getWeatherDetails(String place){
        List<GeoCode> geoCode = getGeoCode(place);
        if(!geoCode.isEmpty()){
            String weather = getWeather(geoCode).replace("\"", "");
            String aqi = getAqi(geoCode);
            WeatherDetails weatherDetails = new WeatherDetails();
            weatherDetails.setWeather(weather);
            weatherDetails.setState(geoCode.get(0).getState());
            weatherDetails.setCountry(geoCode.get(0).getCountry());
            weatherDetails.setPlace(place);
            weatherDetails.setAqi(aqi);
            return weatherDetails;
        }
        return null;
    }

    private String getWeather(List<GeoCode> geoCode) {
        String weatherUrl = MessageFormat.format("https://api.openweathermap.org/data/2.5/weather?lat={0}&lon={1}&appid={2}", geoCode.get(0).getLat(), geoCode.get(0).getLon(),apiKey);
        String jsonResponse = restTemplate.getForObject(weatherUrl,String.class);
        log.info("jsonResp "+jsonResponse);
        try{
            return objectMapper.readTree(jsonResponse).get("weather").get(0).get("description").toString();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    private String getAqi(List<GeoCode> geoCode) {
        String aqiUrl = MessageFormat.format("http://api.openweathermap.org/data/2.5/air_pollution?lat={0}&lon={1}&appid={2}", geoCode.get(0).getLat(), geoCode.get(0).getLon(),apiKey);
        String jsonResponse = restTemplate.getForObject(aqiUrl,String.class);
        log.info("jsonResp "+jsonResponse);
        try{
            log.info("aqi "+objectMapper.readTree(jsonResponse).get("list").get(0).get("main").get("aqi").toString());
            return objectMapper.readTree(jsonResponse).get("list").get(0).get("main").get("aqi").toString();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    private List<GeoCode> getGeoCode(String place) {
        String geoCodeUrl = String.format("http://api.openweathermap.org/geo/1.0/direct?q=%s&appid=%s", place,apiKey);
        String jsonResponse = restTemplate.getForObject(geoCodeUrl, String.class);
        try{
            return objectMapper.readValue(jsonResponse, new TypeReference<List<GeoCode>>() {
            });
        }catch (JsonProcessingException ex){
            throw new RuntimeException(ex);
        }
    }

}
