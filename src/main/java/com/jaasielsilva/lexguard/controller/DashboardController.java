package com.jaasielsilva.lexguard.controller;

import com.jaasielsilva.lexguard.dto.dashboard.DashboardMetricsResponse;
import com.jaasielsilva.lexguard.service.DashboardService;
import com.jaasielsilva.lexguard.tenant.TenantContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DashboardMetricsResponse> getMetrics(@RequestHeader("X-Empresa-Id") Long empresaId) {
        TenantContext.setEmpresaId(empresaId);
        try {
            return ResponseEntity.ok(dashboardService.getMetrics());
        } finally {
            TenantContext.clear();
        }
    }
}
