package raven.modal.demo.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import raven.modal.demo.models.EnumDiscountType;
import raven.modal.demo.models.ModelMenu;
import raven.modal.demo.models.ModelPromotion;
import raven.modal.demo.services.ServicePromotion;
import raven.modal.demo.services.impls.ServiceImplPromotion;
import raven.modal.demo.utils.BusinessException;
import raven.modal.demo.utils.table.ThumbnailCell;

public class ControllerPromotion {

	// Using a service implementation for handling promotion operations
	private ServicePromotion servicePromotion = new ServiceImplPromotion();
	private ControllerMenu controllerMenu = new ControllerMenu();
	public Object[] getBasicRow (ModelPromotion promotion) throws IOException {
		ModelMenu menu = controllerMenu.getMenuById(promotion.getMenuId());
		String discountValue = null;
		String discountedValue = null;
		if (promotion.getDiscountType() == EnumDiscountType.PERCENT) {
			discountValue = promotion.getDiscountValue() + "%";
			discountedValue = menu.getSalePrice() - (menu.getSalePrice() * promotion.getDiscountValue() / 100) + "";
		}
		else if(promotion.getDiscountType() == EnumDiscountType.FLAT) {
			discountValue = promotion.getDiscountValue() + "";
			discountedValue = menu.getSalePrice() - promotion.getDiscountValue() + "";
		}
		return new Object[] {false, promotion.getId(),
				new ThumbnailCell(null, promotion.getName(), promotion.getStatus().getDisplayName(), null),
				discountValue,
				promotion.getStartDate(), promotion.getEndDate(), menu.getThumbnailCell(),menu.getOriginalPrice(),menu.getSalePrice(),discountedValue, promotion.getCreatedTime(), promotion.getModifiedTime()};
	}

	/**
	 * Retrieves a list of all promotions.
	 * 
	 * @return List of all promotions.
	 * @throws IOException if there is an issue during the process.
	 */
	public List<ModelPromotion> getAllPromotions() throws IOException {
		return servicePromotion.getAllPromotions();
	}

	/**
	 * Retrieves a promotion by its ID.
	 * 
	 * @param idHolder The ID of the promotion to retrieve.
	 * @return The promotion with the specified ID.
	 * @throws IOException if there is an issue during the process.
	 */
	public ModelPromotion getPromotionById(long idHolder) throws IOException {
		return servicePromotion.getPromotionById(idHolder);
	}

	/**
	 * Creates a new promotion with the specified details.
	 * 
	 * @param promotionData The data of the promotion to create.
	 * @throws IOException if there is an issue during the process.
	 */
	public void createPromotions(List<Object[]> promotionData) throws IOException {
	    List<ModelPromotion> promotions = promotionData.parallelStream().map(promo ->
	        ModelPromotion.builder()
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
	 * Updates an existing promotion.
	 * 
	 * @param promotionData The updated data of the promotion.
	 * @throws IOException       if there is an issue during the process.
	 * @throws BusinessException if the promotion does not exist.
	 */
	//		return new Object[] {(long) modelPromotion.getId(), txtName.getText(), EnumDiscountType.fromDisplayName(cmbDiscountType.getSelectedItem().toString()), txtDiscountValue.getDoubleValue(), txtDiscountedPrice.getDoubleValue(),startDatePicker.getSelectedDate(), endDatePicker.getSelectedDate() };

	public void updatePromotion(Object[] promotionData) throws IOException, BusinessException {
		ModelPromotion promotion = getPromotionById((long) promotionData[0]);
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
	public List<ModelPromotion> searchPromotions(String search) throws IOException {
		return servicePromotion.searchPromotions(search);
	}

}
