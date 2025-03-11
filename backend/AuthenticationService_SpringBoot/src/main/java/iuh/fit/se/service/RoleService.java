package iuh.fit.se.service;

import iuh.fit.se.dto.request.RoleRequest;
import iuh.fit.se.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {
    RoleResponse create(RoleRequest request);

    List<RoleResponse> getAll();

    void delete(String roleName);
}
