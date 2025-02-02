package raven.modal.demo.services;

import raven.modal.demo.models.ModelPromotion;

import java.io.IOException;
import java.util.List;

/**
 * Service interface for handling Promotion-related operations.
 */
public interface ServicePromotion {

    /**
     * Retrieve all promotions from the server.
     *
     * @return List of all promotions.
     * @throws IOException If an I/O error occurs during the request.
     */
    List<ModelPromotion> getAllPromotions() throws IOException;

    /**
     * Retrieve a promotion by its ID.
     *
     * @param idHolder The ID of the promotion.
     * @return The promotion object.
     * @throws IOException If an I/O error occurs during the request.
     */
    ModelPromotion getPromotionById(long idHolder) throws IOException;

    /**
     * Create a new promotion with the provided data.
     *
     * @param modelPromotion The promotion object containing data to create.
     * @throws IOException If an I/O error occurs during the request.
     */
    void createPromotions(List<ModelPromotion> modelPromotions) throws IOException;

    /**
     * Update an existing promotion by its ID.
     *
     * @param modelPromotion The updated promotion object.
     * @throws IOException If an I/O error occurs during the request.
     */
    void updatePromotion(ModelPromotion modelPromotion) throws IOException;

    /**
     * Delete a promotion by its ID.
     *
     * @param tableId The ID of the promotion to delete.
     * @throws IOException If an I/O error occurs during the request.
     */
    void deletePromotion(long tableId) throws IOException;

    /**
     * Delete multiple promotions by their IDs.
     *
     * @param tableIds List of promotion IDs to delete.
     * @throws IOException If an I/O error occurs during the request.
     */
    void deletePromotions(List<Long> tableIds) throws IOException;

    /**
     * Search for promotions using a query string.
     *
     * @param search The query string.
     * @return List of matching promotions.
     * @throws IOException If an I/O error occurs during the request.
     */
    List<ModelPromotion> searchPromotions(String search) throws IOException;
}
