package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service;

import java.util.List;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Menu;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.ApiResponse;

public interface MenuService {
	ApiResponse list(String keyword, String sortBy, String sortDirection, int page, int size, String statusFilter);
    ApiResponse getMenuById(String id);
    ApiResponse searchMenus(String search);
    ApiResponse createMenu(Menu menu);
    ApiResponse updateMenu(Menu menu);
    ApiResponse removeMenu(String id);
    ApiResponse removeMenus(List<String> ids);
}
