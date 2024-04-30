package com.example.rentitbackend.controller;

import com.example.rentitbackend.dto.ApiResponse;
import com.example.rentitbackend.security.AdminAuthorize;
import com.example.rentitbackend.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "관리자용 API")
@PreAuthorize("hasAuthority('ADMIN')")
@AdminAuthorize
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;

    @Operation(summary = "회원 목록 조회")
    @GetMapping("/members")
    public ApiResponse getAllMembers() {
        return ApiResponse.success(adminService.getMembers());
    }

    @Operation(summary = "관리자 목록 조회")
    @GetMapping("/admins")
    public ApiResponse getAllAdmins() {
        return ApiResponse.success(adminService.getAdmins());
    }
}
