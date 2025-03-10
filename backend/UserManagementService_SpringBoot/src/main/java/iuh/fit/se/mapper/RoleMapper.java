package iuh.fit.se.mapper;

import iuh.fit.se.dto.request.PermissionRequest;
import iuh.fit.se.dto.request.RoleRequest;
import iuh.fit.se.dto.response.PermissionResponse;
import iuh.fit.se.dto.response.RoleResponse;
import iuh.fit.se.entity.Permission;
import iuh.fit.se.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleResponse toRoleResponse(Role role);
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);
}
