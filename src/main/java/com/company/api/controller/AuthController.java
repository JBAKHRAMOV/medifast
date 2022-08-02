package com.company.api.controller;

import com.company.api.dto.authorization.AuthDTO;
import com.company.api.dto.authorization.AuthResponseDTO;
import com.company.api.service.AuthorizationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/authorization/")
@Api(tags = "authorization")
public class AuthController {
    private final AuthorizationService authorizationService;

    @PostMapping("/login")
    @ApiOperation(value = "Login", notes = "method for login ")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthDTO dto) {
        return ResponseEntity.ok(authorizationService.login(dto));
    }
}
