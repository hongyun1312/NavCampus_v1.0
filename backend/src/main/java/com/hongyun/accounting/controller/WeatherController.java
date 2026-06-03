package com.hongyun.accounting.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/weather")
@CrossOrigin(origins = "*")
public class WeatherController {

    private final RestTemplate restTemplate = new RestTemplate();
    
    private static final Map<String, double[]> CITIES = new HashMap<>();
    
    static {
        // lat, lon
        CITIES.put("shenyang", new double[]{41.8057, 123.4315});
        CITIES.put("beijing", new double[]{39.9042, 116.4074});
        CITIES.put("shanghai", new double[]{31.2304, 121.4737});
        CITIES.put("guangzhou", new double[]{23.1291, 113.2644});
        CITIES.put("chengdu", new double[]{30.5728, 104.0668});
    }

    @GetMapping
    public Object getWeather(@RequestParam(defaultValue = "shenyang") String city) {
        double[] coords = CITIES.getOrDefault(city.toLowerCase(), CITIES.get("shenyang"));
        String url = String.format(
            "https://api.open-meteo.com/v1/forecast?latitude=%.4f&longitude=%.4f&hourly=temperature_2m,relativehumidity_2m,windspeed_10m,weathercode&daily=weathercode,temperature_2m_max,temperature_2m_min,sunrise,sunset&current_weather=true&timezone=auto",
            coords[0], coords[1]
        );
        
        try {
            return restTemplate.getForObject(url, Object.class);
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", "Failed to fetch weather data");
        }
    }
}
