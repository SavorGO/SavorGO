package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.impl;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Invoice;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.InvoiceService;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.util.HttpUtil;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.util.JsonUtil;
import okhttp3.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of the InvoiceService interface for interacting with the invoice API.
 * This class uses HttpUtil and JsonUtil for HTTP requests and JSON processing.
 */
public class InvoiceServiceImpl implements InvoiceService {

    private static final String API_URL = "http://localhost:8000/api/invoices";
    private static final String API_URL_ID = API_URL + "/";

    /**
     * Retrieves all invoices from the backend API.
     *
     * @return A list of Invoice objects.
     * @throws IOException If the request fails.
     */
    @Override
    public List<Invoice> getAllInvoices() throws IOException {
        Response response = HttpUtil.get(API_URL);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        Invoice[] invoices = JsonUtil.fromJson(response.body().string(), Invoice[].class);
        return Arrays.asList(invoices);
    }

    /**
     * Retrieves an invoice by its ID from the backend API.
     *
     * @param id The ID of the invoice to retrieve.
     * @return The Invoice object.
     * @throws IOException If the request fails.
     */
    @Override
    public Invoice getInvoiceById(String id) throws IOException {
        String url = API_URL_ID + id;
        Response response = HttpUtil.get(url);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        return JsonUtil.fromJson(response.body().string(), Invoice.class);
    }

    /**
     * Creates a new invoice in the backend API.
     *
     * @param invoice The Invoice object to create.
     * @throws IOException If the request fails.
     */
    @Override
    public void createInvoice(Invoice invoice) throws IOException {
        String jsonBody = JsonUtil.toJson(invoice);
        Response response = HttpUtil.post(API_URL, jsonBody);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
    }

    /**
     * Updates an existing invoice in the backend API.
     *
     * @param invoice The Invoice object to update.
     * @throws IOException If the request fails.
     */
    @Override
    public void updateInvoice(Invoice invoice) throws IOException {
        String jsonBody = JsonUtil.toJson(invoice);
        String url = API_URL_ID + invoice.getId();
        Response response = HttpUtil.put(url, jsonBody);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
    }

    /**
     * Deletes an invoice by its ID from the backend API.
     *
     * @param id The ID of the invoice to delete.
     * @throws IOException If the request fails.
     */
    @Override
    public void deleteInvoice(String id) throws IOException {
        String url = API_URL_ID + id;
        Response response = HttpUtil.delete(url);
        if (!response.isSuccessful()) {
            throw new IOException("Failed to delete invoice with ID: " + id + ". Unexpected code: " + response.code());
        }
    }

    /**
     * Deletes multiple invoices by their IDs from the backend API.
     *
     * @param ids The list of IDs of the invoices to delete.
     * @throws IOException If the request fails.
     */
    @Override
    public void deleteInvoices(List<String> ids) throws IOException {
        String json = "{\"ids\": " + ids.toString() + "}";
        Response response = HttpUtil.deleteWithBody(API_URL, json);
        if (!response.isSuccessful()) {
            throw new IOException("Failed to delete invoices. Unexpected code: " + response.code());
        }
    }

    /**
     * Searches for invoices based on a search term in the backend API.
     *
     * @param search The search term.
     * @return A list of Invoice objects that match the search term.
     * @throws IOException If the request fails.
     */
    @Override
    public List<Invoice> searchInvoices(String search) throws IOException {
        String url = API_URL + "/search?q=" + search;
        Response response = HttpUtil.get(url);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        Invoice[] invoices = JsonUtil.fromJson(response.body().string(), Invoice[].class);
        return Arrays.asList(invoices);
    }
}