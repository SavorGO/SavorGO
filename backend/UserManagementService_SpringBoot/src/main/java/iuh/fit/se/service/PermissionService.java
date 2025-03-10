package iuh.fit.se.service;

import iuh.fit.se.dto.request.PermissionRequest;
import iuh.fit.se.dto.response.PermissionResponse;

import java.util.List;

public interface PermissionService {
    PermissionResponse createPermission(PermissionRequest request);

    List<PermissionResponse> getAllPermissions();

    void deletePermission(String permissionName);
}
