package iuh.fit.se.controller;

import iuh.fit.se.dto.request.PermissionRequest;
import iuh.fit.se.dto.request.RoleRequest;
import iuh.fit.se.dto.response.ApiResponse;
import iuh.fit.se.dto.response.PermissionResponse;
import iuh.fit.se.dto.response.RoleResponse;
import iuh.fit.se.service.PermissionService;
import iuh.fit.se.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class RoleController {
    RoleService roleService;


    @PostMapping
    ApiResponse<RoleResponse> createRole(@RequestBody RoleRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<RoleResponse>> getAllRoles() {
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAll())
                .build();
    }

    @DeleteMapping("/{roleName}")
    ApiResponse<Void> deleteRole(@PathVariable String roleName) {
        roleService.delete(roleName);
        return ApiResponse.<Void>builder()
                .message("Role deleted " + roleName)
                .build();
    }

}
