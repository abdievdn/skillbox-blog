package main.api.response.general;

import lombok.Data;
import main.api.response.BlogResponse;

import java.util.Map;
import java.util.TreeSet;

@Data
public class CalendarResponse implements BlogResponse {

    private TreeSet<Integer> years;
    private Map<String, Integer> posts;
}
