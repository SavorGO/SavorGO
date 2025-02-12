package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.impl;

import okhttp3.Headers;
import okhttp3.Response;
import java.io.IOException;
import java.util.Map;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.User;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.AuthenticationService;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.util.HttpUtil;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.util.JsonUtil;

public class AuthenticationServiceImpl implements AuthenticationService {
    private static final String API_URL = "http://localhost:8080/api/authentication";

    @Override
    public String loginWithGoogle(String idToken) throws IOException {
        String requestBody = String.format("{\"token\":\"%s\"}", idToken);
        System.out.println("Request JSON: " + requestBody);

        Response response = HttpUtil.post(API_URL + "/login-google", requestBody);

        if (response.isSuccessful() && response.body() != null) {
            String responseBody = response.body().string();
            System.out.println("Response: " + responseBody);

            Map<String, String> responseMap = JsonUtil.fromJson(responseBody, Map.class);
            return responseMap.getOrDefault("jwt_token", null);
        } else {
            handleErrorResponse(response);
            return null; // Dòng này không bao giờ chạy nhưng cần để tránh lỗi biên dịch
        }
    }

    @Override
    public String loginWithEmailPassword(String email, String password) throws IOException {
        String requestBody = String.format("{\"email\":\"%s\", \"password\":\"%s\"}", email, password);
        System.out.println("Request JSON: " + requestBody);

        Response response = HttpUtil.post(API_URL + "/login-email-password", requestBody);

        if (response.isSuccessful() && response.body() != null) {
            String responseBody = response.body().string();
            System.out.println("Response: " + responseBody);

            Map<String, String> responseMap = JsonUtil.fromJson(responseBody, Map.class);
            return responseMap.getOrDefault("jwt_token", null);
        } else {
            handleErrorResponse(response);
            return null;
        }
    }

    @Override
    public User verifyJwtToken(String jwtToken) throws IOException {
        Headers headers = new Headers.Builder()
                .add("Authorization", "Bearer " + jwtToken)
                .build();

        Response response = HttpUtil.get(API_URL + "/verify-jwt", headers);

        if (response.isSuccessful() && response.body() != null) {
            String responseBody = response.body().string();
            System.out.println("User Response: " + responseBody);

            return JsonUtil.fromJson(responseBody, User.class);
        } else {
            handleErrorResponse(response);
            return null;
        }
    }

    /**
     * Xử lý lỗi từ API backend và ném `AuthenticationException` với thông tin chi tiết.
     */
    private void handleErrorResponse(Response response) throws IOException {
        String errorMessage = "Unexpected error";
        if (response.body() != null) {
            Map<String, String> errorResponse = JsonUtil.fromJson(response.body().string(), Map.class);
            errorMessage = errorResponse.getOrDefault("error", response.message());
        }

        switch (response.code()) {
            case 400:
                throw new AuthenticationException("Bad Request: " + errorMessage);
            case 401:
                throw new AuthenticationException("Unauthorized: " + errorMessage);
            case 403:
                throw new AuthenticationException("Forbidden: " + errorMessage);
            case 404:
                throw new AuthenticationException("Not Found: " + errorMessage);
            case 500:
                throw new AuthenticationException("Internal Server Error: " + errorMessage);
            default:
                throw new AuthenticationException("Error " + response.code() + ": " + errorMessage);
        }
    }

    @Override
    public void changePassword(String token, String oldPassword, String newPassword) throws IOException {
        // Tạo request body với cả oldPassword và newPassword
        String requestBody = String.format("{\"old_password\":\"%s\", \"new_password\":\"%s\"}", oldPassword, newPassword);
        System.out.println("Request JSON: " + requestBody);

        // Tạo headers với Authorization token
        Headers headers = new Headers.Builder()
            .add("Authorization", "Bearer " + token)
            .build();

        // Gửi request POST với body và headers
        Response response = HttpUtil.post(API_URL + "/change-password", requestBody, headers);

        // Kiểm tra phản hồi từ server
        if (!response.isSuccessful()) {
            handleErrorResponse(response);
        } else {
            System.out.println("Password changed successfully.");
        }
    }


}

class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}
