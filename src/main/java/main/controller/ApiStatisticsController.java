package main.controller;

import lombok.AllArgsConstructor;
import main.api.response.statistics.StatisticsResponse;
import main.service.StatisticsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/statistics")
@AllArgsConstructor
public class ApiStatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/all")
    public ResponseEntity<StatisticsResponse> statisticsAll(Principal principal) {
        StatisticsResponse statisticsResponse = statisticsService.getStatisticsAll(principal);
        return statisticsResponse != null ? ResponseEntity.ok(statisticsResponse) : new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("/my")
    public ResponseEntity<StatisticsResponse> statisticsMy(Principal principal) {
        return ResponseEntity.ok(statisticsService.getStatisticsMy(principal));
    }
}