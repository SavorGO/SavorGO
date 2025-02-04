package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.impl;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Promotion;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.PromotionService;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.util.HttpUtil;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.util.JsonUtil;
import okhttp3.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of the PromotionService interface for interacting with the promotion API.
 * This class uses HttpUtil and JsonUtil for HTTP requests and JSON processing.
 */
public class PromotionServiceImpl implements PromotionService {

    private static final String API_URL = "http://localhost:8000/api/promotions";
    private static final String API_URL_ID = API_URL + "/";

    /**
     * Retrieves all promotions from the backend API.
     *
     * @return A list of Promotion objects.
     * @throws IOException If the request fails.
     */
    @Override
    public List<Promotion> getAllPromotions() throws IOException {
        Response response = HttpUtil.get(API_URL);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        Promotion[] promotions = JsonUtil.fromJson(response.body().string(), Promotion[].class);
        return Arrays.asList(promotions);
    }

    /**
     * Retrieves a promotion by its ID from the backend API.
     *
     * @param id The ID of the promotion to retrieve.
     * @return The Promotion object.
     * @throws IOException If the request fails.
     */
    @Override
    public Promotion getPromotionById(long id) throws IOException {
        String url = API_URL_ID + id;
        Response response = HttpUtil.get(url);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        return JsonUtil.fromJson(response.body().string(), Promotion.class);
    }

    /**
     * Creates multiple promotions in the backend API.
     *
     * @param promotions The list of Promotion objects to create.
     * @throws IOException If the request fails.
     */
    @Override
    public void createPromotions(List<Promotion> promotions) throws IOException {
        String jsonBody = JsonUtil.toJson(promotions);
        Response response = HttpUtil.post(API_URL, jsonBody);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
    }

    /**
     * Updates an existing promotion in the backend API.
     *
     * @param promotion The Promotion object to update.
     * @throws IOException If the request fails.
     */
    @Override
    public void updatePromotion(Promotion promotion) throws IOException {
        String jsonBody = JsonUtil.toJson(promotion);
        String url = API_URL_ID + promotion.getId();
        Response response = HttpUtil.put(url, jsonBody);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
    }

    /**
     * Deletes a promotion by its ID from the backend API.
     *
     * @param id The ID of the promotion to delete.
     * @throws IOException If the request fails.
     */
    @Override
    public void deletePromotion(long id) throws IOException {
        String url = API_URL_ID + id;
        Response response = HttpUtil.delete(url);
        if (!response.isSuccessful()) {
            throw new IOException("Failed to delete promotion with ID: " + id + ". Unexpected code: " + response.code());
        }
    }

    /**
     * Deletes multiple promotions by their IDs from the backend API.
     *
     * @param ids The list of IDs of the promotions to delete.
     * @throws IOException If the request fails.
     */
    @Override
    public void deletePromotions(List<Long> ids) throws IOException {
        String json = "{\"ids\": " + ids.toString() + "}";
        Response response = HttpUtil.deleteWithBody(API_URL, json);
        if (!response.isSuccessful()) {
            throw new IOException("Failed to delete promotions. Unexpected code: " + response.code());
        }
    }

    /**
     * Searches for promotions based on a search term in the backend API.
     *
     * @param search The search term.
     * @return A list of Promotion objects that match the search term.
     * @throws IOException If the request fails.
     */
    @Override
    public List<Promotion> searchPromotions(String search) throws IOException {
        String url = API_URL + "/search?q=" + search;
        Response response = HttpUtil.get(url);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        Promotion[] promotions = JsonUtil.fromJson(response.body().string(), Promotion[].class);
        return Arrays.asList(promotions);
    }
}