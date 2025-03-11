package iuh.fit.se.repository.httpclient;


import iuh.fit.se.dto.request.UserClientRequest;
import iuh.fit.se.dto.response.UserClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", url = "http://localhost:8082")
public interface UserClient {
    @PostMapping( value = "/users/create",produces = MediaType.APPLICATION_JSON_VALUE)
    UserClientResponse createUser(@RequestBody UserClientRequest request);
}
