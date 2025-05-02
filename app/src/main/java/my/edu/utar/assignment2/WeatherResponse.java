package my.edu.utar.assignment2;

import java.util.List;

public class WeatherResponse {
    public Main main;
    public List<Weather> weather;

    public class Main{
        public double temp;
    }

    public class Weather{
        public String description;
        public String icon;
    }
}
