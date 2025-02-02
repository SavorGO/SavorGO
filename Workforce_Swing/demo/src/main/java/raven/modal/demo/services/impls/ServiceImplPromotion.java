package raven.modal.demo.services.impls;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import raven.modal.demo.models.ModelPromotion;
import raven.modal.demo.services.ServicePromotion;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Implementation of the ServicePromotion interface for interacting with the promotion API.
 */
public class ServiceImplPromotion implements ServicePromotion {

    private static final String API_URL = "http://localhost:8000/api/promotions";
    private static final String API_URL_ID = API_URL + "/";
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public ServiceImplPromotion() {
        // Initialize OkHttpClient
        this.client = new OkHttpClient();

        // Configure ObjectMapper for snake_case properties
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    @Override
    public List<ModelPromotion> getAllPromotions() throws IOException {
        Request request = new Request.Builder().url(API_URL).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            ModelPromotion[] promotions = objectMapper.readValue(response.body().string(), ModelPromotion[].class);
            return Arrays.asList(promotions);
        }
    }

    @Override
    public ModelPromotion getPromotionById(long id) throws IOException {
        String url = API_URL_ID + id;
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return objectMapper.readValue(response.body().string(), ModelPromotion.class);
        }
    }

    @Override
    public void createPromotions(List<ModelPromotion> modelPromotions) throws IOException {
        String jsonBody = objectMapper.writeValueAsString(modelPromotions);
        System.out.println(jsonBody);
        Request request = new Request.Builder()
                .url(API_URL)
                .post(RequestBody.create(jsonBody, MediaType.parse("application/json; charset=utf-8")))
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
        }
    }

    @Override
    public void updatePromotion(ModelPromotion modelPromotion) throws IOException {
        String jsonBody = objectMapper.writeValueAsString(modelPromotion);
        Request request = new Request.Builder()
                .url(API_URL_ID + modelPromotion.getId())
                .put(RequestBody.create(jsonBody, MediaType.parse("application/json; charset=utf-8")))
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
        }
    }

    @Override
    public void deletePromotion(long id) throws IOException {
        String url = API_URL_ID + id;
        Request request = new Request.Builder().url(url).delete().build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to delete promotion with ID: " + id);
            }
        }
    }

    @Override
    public void deletePromotions(List<Long> ids) throws IOException {
        String json = "{\"ids\": " + ids.toString() + "}";
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(API_URL).delete(body).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to delete promotions. Unexpected code: " + response.code());
            }
        }
    }

    @Override
    public List<ModelPromotion> searchPromotions(String search) throws IOException {
        String url = API_URL + "/search?q=" + search;
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            ModelPromotion[] promotions = objectMapper.readValue(response.body().string(), ModelPromotion[].class);
            return Arrays.asList(promotions);
        }
    }
}
