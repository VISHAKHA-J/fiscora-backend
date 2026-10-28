package com.fiscora.controller;

import com.fiscora.dto.dashboard.DashboardSummaryResponse;
import com.fiscora.entity.User;
import com.fiscora.service.DashboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fiscora.dto.dashboard.MonthlyTotal;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // GET /api/dashboard/summary  or  GET /api/dashboard/summary?month=2026-10
    @GetMapping("/summary")
    public DashboardSummaryResponse summary(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return dashboardService.summary(user, month);
    }

    // GET /api/dashboard/trend  or  GET /api/dashboard/trend?months=12
    @GetMapping("/trend")
    public List<MonthlyTotal> trend(@AuthenticationPrincipal User user,
                                    @RequestParam(defaultValue = "6") int months) {
        return dashboardService.trend(user, months);
    }

}