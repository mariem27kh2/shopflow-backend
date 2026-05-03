package com.shopflow.shopflow.controller;

import com.shopflow.shopflow.dto.response.DashboardResponse;
import com.shopflow.shopflow.entity.Role;
import com.shopflow.shopflow.repository.UserRepository;
import com.shopflow.shopflow.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserRepository userRepository;

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

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(Map.of(
            "nbSellers", userRepository.countByRole(Role.SELLER),
            "nbCustomers", userRepository.countByRole(Role.CUSTOMER)
        ));
    }
}