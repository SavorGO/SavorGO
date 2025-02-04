package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.impl;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Table;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.TableService;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.util.HttpUtil;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.util.JsonUtil;
import okhttp3.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of the ServiceTable interface.
 * This class handles the communication with the backend API for table-related operations.
 */
public class TableServiceImpl implements TableService {
    private static final String API_URL = "http://localhost:8000/api/tables";
    private static final String API_URL_ID = API_URL + "/";

    /**
     * Retrieves all tables from the backend API.
     *
     * @return A list of ModelTable objects.
     * @throws IOException If the request fails.
     */
    @Override
    public List<Table> getAllTables() throws IOException {
        Response response = HttpUtil.get(API_URL);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        Table[] tables = JsonUtil.fromJson(response.body().string(), Table[].class);
        return Arrays.asList(tables);
    }

    /**
     * Retrieves a table by its ID from the backend API.
     *
     * @param id The ID of the table to retrieve.
     * @return The ModelTable object.
     * @throws IOException If the request fails.
     */
    @Override
    public Table getTableById(Long id) throws IOException {
        String url = API_URL_ID + id;
        Response response = HttpUtil.get(url);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        return JsonUtil.fromJson(response.body().string(), Table.class);
    }

    /**
     * Creates a new table in the backend API.
     *
     * @param table The ModelTable object to create.
     * @throws IOException If the request fails.
     */
    @Override
    public void createTable(Table table) throws IOException {
        String jsonBody = JsonUtil.toJson(table);
        Response response = HttpUtil.post(API_URL, jsonBody);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
    }

    /**
     * Updates an existing table in the backend API.
     *
     * @param modelTable The ModelTable object to update.
     * @throws IOException If the request fails.
     */
    @Override
    public void updateTable(Table modelTable) throws IOException {
        String jsonBody = JsonUtil.toJson(modelTable);
        String url = API_URL_ID + modelTable.getId();
        Response response = HttpUtil.put(url, jsonBody);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
    }

    /**
     * Removes a table by its ID from the backend API.
     *
     * @param id The ID of the table to remove.
     * @throws IOException If the request fails.
     */
    @Override
    public void removeTable(long id) throws IOException {
        String url = API_URL_ID + id;
        Response response = HttpUtil.delete(url);
        if (!response.isSuccessful()) {
            throw new IOException("Failed to delete table with ID: " + id + ". Unexpected code: " + response.code());
        }
    }

    /**
     * Removes multiple tables by their IDs from the backend API.
     *
     * @param ids The list of IDs of the tables to remove.
     * @throws IOException If the request fails.
     */
    @Override
    public void removeTables(List<Long> ids) throws IOException {
        String json = "{\"ids\": " + ids.toString() + "}";
        Response response = HttpUtil.deleteWithBody(API_URL, json);
        if (!response.isSuccessful()) {
            throw new IOException("Failed to delete tables. Unexpected code: " + response.code());
        }
    }

    /**
     * Searches for tables based on a search term in the backend API.
     *
     * @param search The search term.
     * @return A list of ModelTable objects that match the search term.
     * @throws IOException If the request fails.
     */
    @Override
    public List<Table> searchTables(String search) throws IOException {
        String url = API_URL + "/search?q=" + search;
        Response response = HttpUtil.get(url);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        Table[] tables = JsonUtil.fromJson(response.body().string(), Table[].class);
        return Arrays.asList(tables);
    }
}