package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.PromotionDiscountTypeEnum;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Menu;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Promotion;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.PromotionService;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.impl.PromotionServiceImpl;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.table.ThumbnailCell;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.ApiResponse;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.BusinessException;
import raven.modal.Toast;

public class PromotionController {

    // Using a service implementation for handling promotion operations
    private PromotionService promotionService = new PromotionServiceImpl();
    private MenuController menuController = new MenuController();

    public ApiResponse list(String keyword, String sortBy, String sortDirection, int page, int size, String categoryFilter) {
        return promotionService.list(keyword, sortBy, sortDirection, page, size, categoryFilter);
    }
    
    public ApiResponse getPromotionById(long idHolder){
        return promotionService.getPromotionById(idHolder);
    }
    
    public Menu getMenuByPromotion(Promotion promotion) throws IOException {
        ApiResponse apiResponse = menuController.getMenuById(promotion.getMenuId());
        
        if (apiResponse == null || apiResponse.getErrors() != null || apiResponse.getData() == null) {
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Menu menu = objectMapper.convertValue(apiResponse.getData(), Menu.class);
        
        return menu;
    }
    
    public Object[] getTableRow(Promotion promotion) throws IOException {
    	Menu menu = getMenuByPromotion(promotion);
    	
    	String discountValue = null;

        if (promotion.getPromotionDiscountTypeEnum() == PromotionDiscountTypeEnum.PERCENTAGE) {
            discountValue = promotion.getDiscountValue() + "%";
        } else if (promotion.getPromotionDiscountTypeEnum() == PromotionDiscountTypeEnum.FIXED_AMOUNT) {
            discountValue = promotion.getDiscountValue() + "";
        }

        return new Object[] {
            false,
            promotion.getId(),
            new ThumbnailCell(null, promotion.getName(), promotion.getStatus().getDisplayName(), null),
            discountValue,
            promotion.getStartDate(),
            promotion.getEndDate(),
            menuController.getThumbnailCell(menu),
            menu.getOriginalPrice(),
            menu.getSalePrice(),
            menu.getDiscountedPrice(),
            promotion.getCreatedTime(),
            promotion.getModifiedTime()
        };
    }

    public ApiResponse createPromotions(List<Object[]> promotionData) {
        List<Promotion> promotions = promotionData.parallelStream().map(promo ->
            Promotion.builder()
                .menuId((String) promo[0])
                .promotionDiscountTypeEnum(PromotionDiscountTypeEnum.fromDisplayName((String) promo[1]))
                .discountValue((Double) promo[2])
                .startDate((LocalDate) promo[4])
                .endDate((LocalDate) promo[5])
                .name((String) promo[6])
                .build()
        ).collect(Collectors.toList());
        return promotionService.createPromotions(promotions);
    }

    public ApiResponse updatePromotion(Object[] promotionData){
        ApiResponse response = promotionService.getPromotionById((long)promotionData[0]);
        if (response.getStatus() != 200) {
            return response;
        }
        Promotion promotion = (Promotion) response.getData();
    	promotion.setName((String) promotionData[1]);
        promotion.setPromotionDiscountTypeEnum(PromotionDiscountTypeEnum.fromDisplayName((String) promotionData[2]));
        promotion.setDiscountValue((Double) promotionData[3]);
        promotion.setStartDate((LocalDate) promotionData[4]);
        promotion.setEndDate((LocalDate) promotionData[5]);
        return promotionService.updatePromotion(promotion);
    }

    public ApiResponse deletePromotion(long promotionId){
        return promotionService.deletePromotion(promotionId);
    }

    public ApiResponse deletePromotions(List<Long> promotionIds){
        return promotionService.deletePromotions(promotionIds);
    }
}