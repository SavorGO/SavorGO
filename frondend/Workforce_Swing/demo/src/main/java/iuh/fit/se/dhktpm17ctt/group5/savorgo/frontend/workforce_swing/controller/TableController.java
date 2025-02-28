package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller;

import java.time.LocalDateTime;
import java.util.List;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.TableStatusEnum;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Table;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.TableService;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.impl.TableServiceImpl;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.table.ThumbnailCell;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.ApiResponse;

public class TableController {
    private final TableService tableService = new TableServiceImpl();

    public ApiResponse list(String keyword, String sortBy, String sortDirection, int page, int size, String statusFilter) {
        return tableService.list(keyword, sortBy, sortDirection, page, size, statusFilter);
    }

    public ApiResponse getTableById(Long id) {
        return tableService.getTableById(id);
    }

    public ApiResponse createTable(Object[] tableData) {
        Table table = Table.builder().name(tableData[0].toString()).build();
        return tableService.createTable(table);
    }

    public ApiResponse updateTable(Object[] tableData) {
        ApiResponse response = tableService.getTableById((long) tableData[0]);
        if (response.getStatus() != 200) {
            return response;
        }

        Table table = (Table) response.getData();
        table.setName(tableData[1].toString());
        table.setStatus(TableStatusEnum.fromDisplayName(tableData[2].toString()));
        table.setReservedTime((LocalDateTime) tableData[3]);

        return tableService.updateTable(table);
    }

    public ApiResponse deleteTable(long id) {
        return tableService.removeTable(id);
    }

    public ApiResponse deleteTables(List<Long> ids) {
        return tableService.removeTables(ids);
    }

    public Object[] toTableRow(Table table) {
        return new Object[]{
            false,
            table.getId(),
            new ThumbnailCell("table", table.getName(), table.getStatus().getDisplayName(), null),
            table.isReserved(),
            table.getReservedTime(),
            table.getCreatedTime(),
            table.getModifiedTime()
        };
    }
}
