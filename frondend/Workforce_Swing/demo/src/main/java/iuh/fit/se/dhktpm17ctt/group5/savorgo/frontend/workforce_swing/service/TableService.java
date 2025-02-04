package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service;

import java.io.IOException;
import java.util.List;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Table;

/**
 * Interface for managing table-related operations.
 * This interface defines methods for retrieving, creating, updating, and deleting tables.
 */
public interface TableService {

    /**
     * Retrieves all tables from the backend API.
     *
     * @return A list of all tables.
     * @throws IOException If an I/O error occurs during the request.
     */
    public List<Table> getAllTables() throws IOException;

    /**
     * Retrieves a table by its ID.
     *
     * @param id The ID of the table to retrieve.
     * @return The table object.
     * @throws IOException If an I/O error occurs during the request.
     */
    public Table getTableById(Long id) throws IOException;

    /**
     * Searches for tables based on a search term.
     *
     * @param search The search term.
     * @return A list of tables that match the search term.
     * @throws IOException If an I/O error occurs during the request.
     */
    public List<Table> searchTables(String search) throws IOException;

    /**
     * Creates a new table in the backend API.
     *
     * @param table The table object to create.
     * @throws IOException If an I/O error occurs during the request.
     */
    public void createTable(Table table) throws IOException;

    /**
     * Updates an existing table in the backend API.
     *
     * @param table The table object to update.
     * @throws IOException If an I/O error occurs during the request.
     */
    public void updateTable(Table table) throws IOException;

    /**
     * Deletes a table by its ID.
     *
     * @param id The ID of the table to delete.
     * @throws IOException If an I/O error occurs during the request.
     */
    public void removeTable(long id) throws IOException;

    /**
     * Deletes multiple tables by their IDs.
     *
     * @param ids The list of IDs of the tables to delete.
     * @throws IOException If an I/O error occurs during the request.
     */
    public void removeTables(List<Long> ids) throws IOException;
}