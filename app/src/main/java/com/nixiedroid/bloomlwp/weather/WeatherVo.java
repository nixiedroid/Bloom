package com.nixiedroid.bloomlwp.weather;

import org.jetbrains.annotations.NotNull;

public class WeatherVo {
    private static final String[] conditionStrings =
            new String[]{
                    "UNKNOWN",
                    "CLEAR",
                    "CLOUDY",
                    "FOGGY",
                    "HAZY",
                    "ICY",
                    "RAINY",
                    "SNOWY",
                    "STORMY",
                    "WINDY"
    };
    public int[] conditions;

    public static String conditionString(int n) {
        return conditionStrings[n];
    }

    @NotNull
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("[WeatherVo] conditions: ");
        for (int condition : conditions) {
            output.append(conditionStrings[condition]).append(" (").append(condition).append("), ");
        }
        return output.toString();
    }
}

