package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Promotion;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.PromotionService;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.ApiResponse;

public class PromotionServiceImpl implements PromotionService {
    private static final String API_URL = "http://localhost:8000/api/promotions";
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

	public PromotionServiceImpl() {
		objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
		objectMapper.registerModule(new JavaTimeModule());
	}
    
    
    @Override
    public ApiResponse list(
            String keyword,
            String sortBy,
            String sortDirection,
            int page,
            int size,
            String statusFilter
    ) {
        try {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(API_URL).newBuilder()
                    .addQueryParameter("keyword", keyword != null ? keyword : "")
                    .addQueryParameter("sortBy", sortBy != null ? sortBy : "id")
                    .addQueryParameter("sortDirection", sortDirection != null ? sortDirection : "asc")
                    .addQueryParameter("page", String.valueOf(page > 0 ? page : 1))
                    .addQueryParameter("size", String.valueOf(size > 0 ? size : 10))
                    .addQueryParameter("statusFilter", statusFilter != null ? statusFilter : "without_deleted");

            Request request = new Request.Builder()
                    .url(urlBuilder.build())
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return ApiResponse.builder()
                            .status(response.code())
                            .message("Failed to fetch promotions")
                            .errors(Map.of("error", "Unexpected response code: " + response.code()))
                            .build();
                }
                return objectMapper.readValue(response.body().string(), ApiResponse.class);
            }
        } catch (IOException e) {
            return ApiResponse.builder()
                    .status(500)
                    .message("Internal Server Error")
                    .errors(Map.of("exception", e.getMessage()))
                    .build();
        }
    }

    @Override
    public ApiResponse getPromotionById(long id) {
        try {
            HttpUrl url = HttpUrl.parse(API_URL + "/" + id).newBuilder().build();

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return ApiResponse.builder()
                            .status(response.code())
                            .message("Failed to fetch promotion")
                            .errors(Map.of("error", "Unexpected response code: " + response.code()))
                            .build();
                }
                return objectMapper.readValue(response.body().string(), ApiResponse.class);
            }
        } catch (IOException e) {
            return ApiResponse.builder()
                    .status(500)
                    .message("Internal Server Error")
                    .errors(Map.of("exception", e.getMessage()))
                    .build();
        }
    }

    @Override
    public ApiResponse createPromotions(List<Promotion> promotions) {
        try {
            HttpUrl url = HttpUrl.parse(API_URL).newBuilder().build();

            String jsonBody = objectMapper.writeValueAsString(promotions != null ? promotions : new ArrayList<>());
            RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json"));
            System.out.println(jsonBody);

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                return objectMapper.readValue(response.body().string(), ApiResponse.class);
            }

        } catch (IOException e) {
            return ApiResponse.builder()
                    .status(500)
                    .message("Internal Server Error")
                    .errors(Map.of("exception", e.getMessage()))
                    .build();
        }
    }


    @Override
    public ApiResponse updatePromotion(Promotion promotion) {
        return null;
    }

    @Override
    public ApiResponse deletePromotion(long id) {
        return null;
    }

    @Override
    public ApiResponse deletePromotions(List<Long> ids) {
        return null;
    }
}
