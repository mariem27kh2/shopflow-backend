package com.shopflow.shopflow.controller;

import com.shopflow.shopflow.dto.response.DashboardResponse;
import com.shopflow.shopflow.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardResponse> getAdminDashboard() {
        return ResponseEntity.ok(dashboardService.getAdminDashboard());
    }

    @GetMapping("/seller")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<DashboardResponse> getSellerDashboard() {
        return ResponseEntity.ok(dashboardService.getSellerDashboard());
    }

    @GetMapping("/customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<DashboardResponse> getCustomerDashboard() {
        return ResponseEntity.ok(dashboardService.getCustomerDashboard());
    }
}