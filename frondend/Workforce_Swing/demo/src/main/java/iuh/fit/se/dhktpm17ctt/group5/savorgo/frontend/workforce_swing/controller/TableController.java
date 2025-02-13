package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.TableStatusEnum;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Table;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.TableService;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.impl.TableServiceImpl;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.table.ThumbnailCell;

public class TableController {
    private TableService serviceTable = new TableServiceImpl();

    /**
     * Retrieves a list of all tables.
     * 
     * @return List of all tables.
     * @throws IOException if there is an issue during the process.
     */
    public List<Table> getAllTables() throws IOException {
        return serviceTable.getAllTables();
    }

    /**
     * Retrieves a table by its ID.
     * 
     * @param id The ID of the table to retrieve.
     * @return The table with the specified ID.
     * @throws IOException if there is an issue during the process.
     */
    public Table getTableById(Long id) throws IOException {
        return serviceTable.getTableById(id);
    }

    /**
     * Creates a new table with the specified details.
     * 
     * @param tableData An array containing the details of the table.
     *                  [0] - name.
     * @throws IOException if there is an issue during the process.
     */
    public void createTable(Object[] tableData) throws IOException {
        Table table = Table.builder().name(tableData[0].toString()).build();
        serviceTable.createTable(table);
    }

    /**
     * Updates an existing table with the specified details.
     * 
     * @param tableData An array containing the updated details of the table.
     *                  [0] - id, [1] - name, [2] - status, [3] - reserved time.
     * @throws IOException if there is an issue during the process.
     */
    public void updateTable(Object[] tableData) throws IOException {
        Table table = serviceTable.getTableById((long) tableData[0]);
        table.setName(tableData[1].toString());
        table.setStatus(TableStatusEnum.fromDisplayName(tableData[2].toString()));
        table.setReservedTime((LocalDateTime) tableData[3]);
        serviceTable.updateTable(table);
    }

    /**
     * Deletes a table by its ID.
     * 
     * @param id The ID of the table to delete.
     * @throws IOException if there is an issue during the process.
     */
    public void deleteTable(long id) throws IOException {
        serviceTable.removeTable(id);
    }

    /**
     * Deletes multiple tables by their IDs.
     * 
     * @param ids The list of IDs of the tables to delete.
     * @throws IOException if there is an issue during the process.
     */
    public void deleteTables(List<Long> ids) throws IOException {
        serviceTable.removeTables(ids);
    }

    /**
     * Searches for tables based on a search term.
     * 
     * @param search The search term to filter tables.
     * @return List of tables matching the search term.
     * @throws IOException if there is an issue during the process.
     */
    public List<Table> searchTables(String search) throws IOException {
        return serviceTable.searchTables(search);
    }

    /**
     * Converts a Table object to an array of objects for table display.
     * 
     * @param table The Table object to convert.
     * @return An array of objects suitable for table display.
     */
    public Object[] toTableRow(Table table) {
        return new Object[]{
            false, // Checkbox for selection
            table.getId(),
            new ThumbnailCell("table", table.getName(), table.getStatus().getDisplayName(), null), // ThumbnailCell representation
            table.isReserved(), // Check if the table is reserved
            table.getReservedTime(), // Reserved time
            table.getCreatedTime(), // Created time
            table.getModifiedTime() // Modified time
        };
    }
}