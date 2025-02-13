package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form.controller;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.InvoiceController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Invoice;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form.InvoiceFormUI;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.info.InvoiceInfoForm;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.DefaultComponent;
import raven.modal.ModalDialog;
import raven.modal.Toast;
import raven.modal.component.AdaptSimpleModalBorder;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class InvoiceFormController {
    private InvoiceFormUI formInvoiceUI;
    private InvoiceController invoiceController = new InvoiceController();
    private static final int DEBOUNCE_DELAY = 1000;
    private Timer debounceTimer;
    private volatile boolean isLoading = false;

    /**
     * Constructs an InvoiceFormController with the specified InvoiceFormUI.
     * 
     * @param formInvoice The InvoiceFormUI instance to control.
     */
    public InvoiceFormController(InvoiceFormUI formInvoice) {
        this.formInvoiceUI = formInvoice;
    }

    /**
     * Loads data based on the search term.
     * 
     * @param searchTerm the term to search for invoices.
     */
    public void loadData(String searchTerm) {
        if (isLoading) {
            return;
        }
        isLoading = true;

        SwingUtilities.invokeLater(() -> {
            try {
                List<Invoice> invoices = fetchInvoices(searchTerm);
                if (invoices == null || invoices.isEmpty()) {
                    Toast.show(formInvoiceUI, Toast.Type.INFO, "No invoices found in the database or in search");
                    return;
                }
                populateInvoiceTable(invoices);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                isLoading = false;
            }
        });
    }

    /**
     * Fetches the list of invoices based on the search term.
     * 
     * @param searchTerm The term to search for in the invoices.
     * @return The list of Invoice objects.
     * @throws IOException If an I/O error occurs.
     */
    private List<Invoice> fetchInvoices(String searchTerm) throws IOException {
        if (searchTerm != null && !searchTerm.isEmpty()) {
            return invoiceController.searchInvoices(searchTerm);
        } else {
            return invoiceController.getAllInvoices();
        }
    }

    /**
     * Populates the invoice table with the fetched Invoice objects.
     * 
     * @param invoices The list of Invoice objects to populate.
     */
    private void populateInvoiceTable(List<Invoice> invoices) {
        SwingUtilities.invokeLater(() -> {
            formInvoiceUI.getTableModel().setRowCount(0);
            for (Invoice invoice : invoices) {
                formInvoiceUI.getTableModel().addRow(invoiceController.toTableRow(invoice));
            }
        });
    }

    /**
     * Handles the search text change with debounce functionality.
     * 
     * @param txtSearch The JTextField containing the search text.
     */
    public void handleSearchTextChange(JTextField txtSearch) {
        if (debounceTimer != null && debounceTimer.isRunning()) {
            debounceTimer.stop();
        }
        debounceTimer = new Timer(DEBOUNCE_DELAY, evt -> loadData(txtSearch.getText()));
        debounceTimer.setRepeats(false);
        debounceTimer.start();
    }

    /**
     * Displays a modal dialog for showing details of a selected invoice.
     */
    public void showDetailsModal() {
        String invoiceId = getSelectedInvoiceId();
        if (invoiceId == null)
            return;
        InvoiceInfoForm infoFormInvoice = createInfoFormInvoice(invoiceId);
        ModalDialog.showModal(formInvoiceUI, new AdaptSimpleModalBorder(infoFormInvoice, "Invoice details information",
                AdaptSimpleModalBorder.DEFAULT_OPTION, (controller, action) -> {
                }), DefaultComponent.getInfoForm());
    }

    /**
     * Gets the ID of the selected invoice.
     * 
     * @return The ID of the selected invoice, or null if no invoice is selected.
     */
    private String getSelectedInvoiceId() {
        int selectedRow = formInvoiceUI.getTable().getSelectedRow();
        if (selectedRow == -1) {
            Toast.show(formInvoiceUI, Toast.Type.ERROR, "Please select an invoice to view details");
            return null;
        }
        return formInvoiceUI.getTable().getValueAt(selectedRow, 1).toString(); // Assuming the second column is the invoice ID
    }

    /**
     * Creates an InfoFormInvoice for displaying invoice details.
     * 
     * @param invoiceId The ID of the invoice to display.
     * @return The created InfoFormInvoice, or null if an error occurs.
     */
    private InvoiceInfoForm createInfoFormInvoice(String invoiceId) {
        try {
            return new InvoiceInfoForm(invoiceId);
        } catch (IOException e) {
            Toast.show(formInvoiceUI, Toast.Type.ERROR, "Failed to find invoice to view details: " + e.getMessage());
            return null;
        }
    }

    /**
     * Displays a modal dialog for viewing invoice details.
     */
    public void showModal(String invoiceAction) {
        if ("details".equals(invoiceAction)) {
            showDetailsModal();
        }
    }
}