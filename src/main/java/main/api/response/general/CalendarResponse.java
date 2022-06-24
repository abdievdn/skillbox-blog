package main.api.response.general;

import lombok.Data;

import java.util.Map;
import java.util.TreeSet;

@Data
public class CalendarResponse {
    private TreeSet<Integer> years;
    private Map<String, Integer> posts;
}
