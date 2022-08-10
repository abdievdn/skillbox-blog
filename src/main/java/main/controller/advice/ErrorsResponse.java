package main.controller.advice;

import lombok.Data;

import java.util.Map;

@Data
public class ErrorsResponse {
    boolean result;
    Map<String, String> errors;
}
