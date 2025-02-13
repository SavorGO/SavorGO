package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Invoice;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.InvoiceItem;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Payment;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.InvoiceService;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.impl.InvoiceServiceImpl;

public class InvoiceController {

    // Using a service implementation for handling invoice operations
    private InvoiceService serviceInvoice = new InvoiceServiceImpl();

    /**
     * Retrieves a list of all invoices.
     * 
     * @return List of all invoices.
     * @throws IOException if there is an issue during the process.
     */
    public List<Invoice> getAllInvoices() throws IOException {
        return serviceInvoice.getAllInvoices();
    }

    /**
     * Retrieves an invoice by its ID.
     * 
     * @param id The ID of the invoice to retrieve.
     * @return The invoice with the specified ID.
     * @throws IOException if there is an issue during the process.
     */
    public Invoice getInvoiceById(String id) throws IOException {
        return serviceInvoice.getInvoiceById(id);
    }
    
    /**
     * Converts an Invoice object to an array of objects for table display.
     * 
     * @param invoice The Invoice object to convert.
     * @return An array of objects suitable for table display.
     */
    public Object[] toTableRow(Invoice invoice) {
        return new Object[]{
            false, // Checkbox for selection
            invoice.getId(),
            invoice.getCustomerId(),
            invoice.getStaffId(),
            invoice.getDeliveryAddress(),
            invoice.getOrderTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            invoice.getStatus(),
            invoice.getCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            invoice.getModifiedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            invoice.getTotalDue()
        };
    }

    /**
     * Creates a new invoice with the specified details.
     * 
     * @param invoiceData An array containing the details of the invoice.
     *                    [0] - customer_id, [1] - staff_id, [2] - delivery_address,
     *                    [3] - order_time, [4] - items, [5] - payments, [6] - points,
     *                    [7] - additional_requests, [8] - status, [9] - total_due.
     * @throws IOException if there is an issue during the process.
     */
    public void createInvoice(Object[] invoiceData) throws IOException {
        Invoice invoice = Invoice.builder()
                .customerId(invoiceData[0].toString())
                .staffId(invoiceData[1].toString())
                .deliveryAddress(invoiceData[2].toString())
                .orderTime(LocalDateTime.parse(invoiceData[3].toString()))
                .items((List<InvoiceItem>) invoiceData[4]) // Assuming items is a List<Item>
                .payments((List<Payment>) invoiceData[5]) // Assuming payments is a List<Payment>
                .points((int) invoiceData[6])
                .additionalRequests(invoiceData[7].toString())
                .status(invoiceData[8].toString())
                .totalDue(invoiceData[9].toString())
                .createdTime(LocalDateTime.now())
                .modifiedTime(LocalDateTime.now())
                .build();
        serviceInvoice.createInvoice(invoice);
    }

    /**
     * Updates the invoice information based on the provided data.
     *
     * @param invoiceData An array of objects containing invoice information:
     *                    - invoiceData[0]: Invoice ID (String)
     *                    - invoiceData[1]: Customer ID (String)
     *                    - invoiceData[2]: Staff ID (String)
     *                    - invoiceData[3]: Delivery Address (String)
     *                    - invoiceData[4]: Order Time (LocalDateTime)
     *                    - invoiceData[5]: Items (List<Item>)
     *                    - invoiceData[6]: Payments (List<Payment>)
     *                    - invoiceData[7]: Points (Integer)
     *                    - invoiceData[8]: Additional Requests (String)
     *                    - invoiceData[9]: Status (String)
     *                    - invoiceData[10]: Total Due (String)
     * 
     * @throws IOException if there is an issue during the process.
     */
    public void updateInvoice(Object[] invoiceData) throws IOException {
        Invoice invoice = getInvoiceById(invoiceData[0].toString());
        invoice.setCustomerId(invoiceData[1].toString());
        invoice.setStaffId(invoiceData[2].toString());
        invoice.setDeliveryAddress(invoiceData[3].toString());
        invoice.setOrderTime(LocalDateTime.parse(invoiceData[4].toString()));
        invoice.setItems((List<InvoiceItem>) invoiceData[5]);
        invoice.setPayments((List<Payment>) invoiceData[6]);
        invoice.setPoints((int) invoiceData[7]);
        invoice.setAdditionalRequests(invoiceData[8].toString());
        invoice.setStatus(invoiceData[9].toString());
        invoice.setTotalDue(invoiceData[10].toString());
        invoice.setModifiedTime(LocalDateTime.now());
        serviceInvoice.updateInvoice(invoice);
    }

    /**
     * Deletes an invoice by its ID.
     * 
     * @param id The ID of the invoice to delete.
     * @throws IOException if there is an issue during the process.
     */
    public void deleteInvoice(String id) throws IOException {
        serviceInvoice.deleteInvoice(id);
    }

    /**
     * Deletes multiple invoices by their IDs.
     * 
     * @param invoiceIds The list of IDs of the invoices to delete.
     * @throws IOException if there is an issue during the process.
     */
    public void deleteInvoices(List<String> invoiceIds) throws IOException {
        serviceInvoice.deleteInvoices(invoiceIds);
    }

    /**
     * Searches for invoices based on a search term.
     * 
     * @param search The search term to filter invoices.
     * @return List of invoices matching the search term.
     * @throws IOException if there is an issue during the process.
     */
    public List<Invoice> searchInvoices(String search) throws IOException {
        return serviceInvoice.searchInvoices(search);
    }
}