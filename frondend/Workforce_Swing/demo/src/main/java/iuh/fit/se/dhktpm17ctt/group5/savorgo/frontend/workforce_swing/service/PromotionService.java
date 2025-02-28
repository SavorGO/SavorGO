package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service;

import java.util.List;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Promotion;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.ApiResponse;

public interface PromotionService {
    ApiResponse list(String keyword, String sortBy, String sortDirection, int page, int size, String statusFilter);
    ApiResponse getPromotionById(long id);
    ApiResponse searchPromotions(String search);
    ApiResponse createPromotions(List<Promotion> promotions);
    ApiResponse updatePromotion(Promotion promotion);
    ApiResponse deletePromotion(long id);
    ApiResponse deletePromotions(List<Long> ids);
}
