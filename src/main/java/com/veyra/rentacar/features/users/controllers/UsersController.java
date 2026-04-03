package com.veyra.rentacar.features.users.controllers;

import com.veyra.rentacar.features.auth.dtos.responses.UserResponse;
import com.veyra.rentacar.features.users.abstracts.UserService;
import com.veyra.rentacar.features.users.dtos.responses.AdminUserResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Kullanıcı Yönetimi")
public class UsersController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(userService.getMe(email));
    }

    @GetMapping
    public ResponseEntity<List<AdminUserResponse>> getAllWithStats() {
        return ResponseEntity.ok(userService.getAllWithStats());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminUserResponse> getByIdWithStats(@PathVariable String id) {
        return ResponseEntity.ok(userService.getByIdWithStats(id));
    }
}
