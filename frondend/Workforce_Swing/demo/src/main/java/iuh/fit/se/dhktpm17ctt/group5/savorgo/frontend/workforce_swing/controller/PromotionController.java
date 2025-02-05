package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.EnumDiscountType;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Menu;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Promotion;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.PromotionService;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.impl.PromotionServiceImpl;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.table.ThumbnailCell;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.BusinessException;

public class ControllerPromotion {

    // Using a service implementation for handling promotion operations
    private PromotionService servicePromotion = new PromotionServiceImpl();
    private ControllerMenu controllerMenu = new ControllerMenu();

    /**
     * Retrieves the basic row data for a promotion.
     * 
     * @param promotion The promotion object to retrieve data from.
     * @return An array containing the basic row data for the promotion.
     * @throws IOException if there is an issue during the process.
     */
    public Object[] getBasicRow(Promotion promotion) throws IOException {
        Menu menu = controllerMenu.getMenuById(promotion.getMenuId());
        String discountValue = null;
        String discountedValue = null;

        if (promotion.getDiscountType() == EnumDiscountType.PERCENT) {
            discountValue = promotion.getDiscountValue() + "%";
            discountedValue = menu.getSalePrice() - (menu.getSalePrice() * promotion.getDiscountValue() / 100) + "";
        } else if (promotion.getDiscountType() == EnumDiscountType.FLAT) {
            discountValue = promotion.getDiscountValue() + "";
            discountedValue = menu.getSalePrice() - promotion.getDiscountValue() + "";
        }

        return new Object[] {
            false,
            promotion.getId(),
            new ThumbnailCell(null, promotion.getName(), promotion.getStatus().getDisplayName(), null),
            discountValue,
            promotion.getStartDate(),
            promotion.getEndDate(),
            menu.getThumbnailCell(),
            menu.getOriginalPrice(),
            menu.getSalePrice(),
            discountedValue,
            promotion.getCreatedTime(),
            promotion.getModifiedTime()
        };
    }

    /**
     * Retrieves a list of all promotions.
     * 
     * @return List of all promotions.
     * @throws IOException if there is an issue during the process.
     */
    public List<Promotion> getAllPromotions() throws IOException {
        return servicePromotion.getAllPromotions();
    }

    /**
     * Retrieves a promotion by its ID.
     * 
     * @param idHolder The ID of the promotion to retrieve.
     * @return The promotion with the specified ID.
     * @throws IOException if there is an issue during the process.
     */
    public Promotion getPromotionById(long idHolder) throws IOException {
        return servicePromotion.getPromotionById(idHolder);
    }

    /**
     * Creates new promotions with the specified details.
     * 
     * @param promotionData A list of arrays containing the details of the promotions to create.
     *                      Each array should contain: [menuId, discountType, discountValue, startDate, endDate, name].
     * @throws IOException if there is an issue during the process.
     */
    public void createPromotions(List<Object[]> promotionData) throws IOException {
        List<Promotion> promotions = promotionData.parallelStream().map(promo ->
            Promotion.builder()
                .menuId((String) promo[0])
                .discountType(EnumDiscountType.fromDisplayName((String) promo[1]))
                .discountValue((Double) promo[2])
                .startDate((LocalDate) promo[4])
                .endDate((LocalDate) promo[5])
                .name((String) promo[6])
                .build()
        ).collect(Collectors.toList());
        servicePromotion.createPromotions(promotions);
    }

    /**
     * Updates an existing promotion with the specified details.
     * 
     * @param promotionData An array containing the updated details of the promotion.
     *                      [0] - id, [1] - name, [2] - discount type,
     *                      [3] - discount value, [4] - start date, [5] - end date.
     * @throws IOException       if there is an issue during the process.
     * @throws BusinessException if the promotion does not exist.
     */
    public void updatePromotion(Object[] promotionData) throws IOException, BusinessException {
        Promotion promotion = getPromotionById((long) promotionData[0]);
        promotion.setName((String) promotionData[1]);
        promotion.setDiscountType((EnumDiscountType) promotionData[2]);
        promotion.setDiscountValue((Double) promotionData[3]);
        promotion.setStartDate((LocalDate) promotionData[4]);
        promotion.setEndDate((LocalDate) promotionData[5]);
        servicePromotion.updatePromotion(promotion);
    }

    /**
     * Deletes a promotion by its ID.
     * 
     * @param tableId The ID of the promotion to delete.
     * @throws IOException if there is an issue during the process.
     */
    public void deletePromotion(long tableId) throws IOException {
        servicePromotion.deletePromotion(tableId);
    }

    /**
     * Deletes multiple promotions by their IDs.
     * 
     * @param tableIds The list of IDs of the promotions to delete.
     * @throws IOException if there is an issue during the process.
     */
    public void deletePromotions(List<Long> tableIds) throws IOException {
        servicePromotion.deletePromotions(tableIds);
    }

    /**
     * Searches for promotions based on a search term.
     * 
     * @param search The search term to filter promotions.
     * @return List of promotions matching the search term.
     * @throws IOException if there is an issue during the process.
     */
    public List<Promotion> searchPromotions(String search) throws IOException {
        return servicePromotion.searchPromotions(search);
    }
}