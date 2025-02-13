package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service;

import java.io.IOException;
import java.util.List;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Invoice;

/**
 * Interface for managing invoice-related operations.
 * This interface defines methods for retrieving, creating, updating, and deleting invoices.
 */
public interface InvoiceService {

    /**
     * Retrieves all invoices from the backend API.
     *
     * @return A list of all invoices.
     * @throws IOException If an I/O error occurs during the request.
     */
    public List<Invoice> getAllInvoices() throws IOException;

    /**
     * Retrieves an invoice by its ID.
     *
     * @param id The ID of the invoice to retrieve.
     * @return The invoice object.
     * @throws IOException If an I/O error occurs during the request.
     */
    public Invoice getInvoiceById(String id) throws IOException;

    /**
     * Searches for invoices based on a search term.
     *
     * @param search The search term.
     * @return A list of invoices that match the search term.
     * @throws IOException If an I/O error occurs during the request.
     */
    public List<Invoice> searchInvoices(String search) throws IOException;

    /**
     * Creates a new invoice in the backend API.
     *
     * @param invoice The invoice object to create.
     * @throws IOException If an I/O error occurs during the request.
     */
    public void createInvoice(Invoice invoice) throws IOException;

    /**
     * Updates an existing invoice in the backend API.
     *
     * @param invoice The invoice object to update.
     * @throws IOException If an I/O error occurs during the request.
     */
    public void updateInvoice(Invoice invoice) throws IOException;

    /**
     * Deletes an invoice by its ID.
     *
     * @param id The ID of the invoice to delete.
     * @throws IOException If an I/O error occurs during the request.
     */
    public void deleteInvoice(String id) throws IOException;

    /**
     * Deletes multiple invoices by their IDs.
     *
     * @param ids The list of IDs of the invoices to delete.
     * @throws IOException If an I/O error occurs during the request.
     */
    public void deleteInvoices(List<String> ids) throws IOException;
}