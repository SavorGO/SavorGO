package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service;

import java.util.List;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Table;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.ApiResponse;

public interface TableService {
    ApiResponse list(String keyword, String sortBy, String sortDirection, int page, int size, String statusFilter);
    ApiResponse getTableById(long id);
    ApiResponse createTable(Table table);
    ApiResponse updateTable(Table table);
    ApiResponse deleteTable(long id);
	ApiResponse deleteTables(List<Long> ids);
}
