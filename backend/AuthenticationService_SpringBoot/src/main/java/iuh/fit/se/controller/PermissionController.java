package iuh.fit.se.controller;

import iuh.fit.se.dto.request.PermissionRequest;
import iuh.fit.se.dto.response.ApiResponse;
import iuh.fit.se.dto.response.PermissionResponse;
import iuh.fit.se.entity.Permission;
import iuh.fit.se.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class PermissionController {
    PermissionService permissionService;

    @PostMapping()
    ApiResponse<PermissionResponse> createPermission(@RequestBody PermissionRequest request) {

        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.createPermission(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<PermissionResponse>> getAllPermissions() {
        return ApiResponse.<List<PermissionResponse>>builder()
                .result(permissionService.getAllPermissions())
                .build();
    }

    @DeleteMapping("/{permissionName}")
    ApiResponse<Void> deletePermission(@PathVariable String permissionName) {
        permissionService.deletePermission(permissionName);
        return ApiResponse.<Void>builder()
                .message("Permission deleted " + permissionName)
                .build();
    }
}
