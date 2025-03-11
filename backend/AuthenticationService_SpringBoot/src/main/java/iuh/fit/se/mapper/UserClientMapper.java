package iuh.fit.se.mapper;

import iuh.fit.se.dto.request.UserClientRequest;
import iuh.fit.se.dto.request.UserCreationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserClientMapper {
    // Ánh xạ các trường từ UserCreationRequest sang UserClientRequest
//    @Mapping(source = "id", target = "accountId")
    UserClientRequest toUserClientRequest(UserCreationRequest request);
}
