package com.company.api.controller;

import com.company.api.dto.UpdatePasswordRequestDTO;
import com.company.api.service.AdminApiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/admin")
@RequiredArgsConstructor
@Api(tags = "Api for admin")
public class AdminApiController {
    private final AdminApiService adminService;

    @PutMapping("/update-password")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "update password", notes = "method for changing password")
    public ResponseEntity<?> login(@RequestBody UpdatePasswordRequestDTO dto) {
        return ResponseEntity.ok(adminService.updatePassword(dto));
    }

    @GetMapping("/bot-stats")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "get bot stats", notes = "method for get bot stats")
    public ResponseEntity<?> getBotStats() {
        return ResponseEntity.ok(adminService.getStats());
    }
}
