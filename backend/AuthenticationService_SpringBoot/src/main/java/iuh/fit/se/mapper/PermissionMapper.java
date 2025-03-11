package iuh.fit.se.mapper;

import iuh.fit.se.dto.request.PermissionRequest;
import iuh.fit.se.dto.response.PermissionResponse;
import iuh.fit.se.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    PermissionResponse toPermissionResponse(Permission permission);
    Permission toPermission(PermissionRequest request);
}
