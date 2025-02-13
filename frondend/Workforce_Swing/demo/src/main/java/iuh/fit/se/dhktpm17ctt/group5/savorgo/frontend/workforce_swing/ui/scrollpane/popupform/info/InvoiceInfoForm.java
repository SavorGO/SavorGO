package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.info;

import com.formdev.flatlaf.FlatClientProperties;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.InvoiceController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Invoice;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.PopupFormBasic;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.DefaultComponent;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class InvoiceInfoForm extends PopupFormBasic {
    private InvoiceController invoiceController;
    private Invoice invoice;

    public InvoiceInfoForm(String id) throws IOException {
        invoiceController = new InvoiceController();
        invoice = invoiceController.getInvoiceById(id);
        init();
    }

    @Override
    protected void init() {
        createTitle(); // Create the title and add it to the content panel
        createFields(); // Create fields and add them to the content panel
        setViewportView(contentPanel); // Set the content panel to the scroll pane
    }

    /** 
     * Create the title label for the form.
     */
    @Override
    protected void createTitle() {
        JLabel lb = new JLabel("Invoice Information");
        lb.putClientProperty(FlatClientProperties.STYLE, "font:+2");
        contentPanel.add(lb, "gapy 5 0"); // Add title label to the panel
        contentPanel.add(new JSeparator(), "height 2!,gapy 0 0"); // Add a separator below the title
    }

    /** 
     * Create fields to display the invoice information.
     */
    @Override
    protected void createFields() {
        addField("Invoice ID:", invoice.getId());
        addField("Customer ID:", invoice.getCustomerId());
        addField("Staff ID:", invoice.getStaffId());
        addField("Delivery Address:", invoice.getDeliveryAddress());
        addField("Order Time:", invoice.getOrderTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        addField("Points:", String.valueOf(invoice.getPoints()));
        addField("Additional Requests:", invoice.getAdditionalRequests());
        addField("Status:", invoice.getStatus());
        addField("Total Due:", invoice.getTotalDue());
        addField("Created At:", invoice.getCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        addField("Updated At:", invoice.getModifiedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        // Assuming you have methods to display items and payments
        addListField("Items:", invoice.getItems());
        addListField("Payments:", invoice.getPayments());
    }

    /** 
     * Add a field to the panel with a label and a non-editable text field.
     * @param fieldName The name of the field.
     * @param fieldValue The value of the field.
     */
    private void addField(String fieldName, String fieldValue) {
        contentPanel.add(new JLabel(fieldName), "gapy 5 0"); // Add the field name label
        JTextField textField = new JTextField(fieldValue); // Create a text field with the field value
        textField.setEditable(false); // Make the text field non-editable
        textField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, fieldName); // Set placeholder text
        contentPanel.add(textField); // Add the text field to the panel
    }

    /** 
     * Add a list field to the panel with a label and a non-editable text area.
     * @param fieldName The name of the field.
     * @param list The list of values to display.
     */
    private void addListField(String fieldName, List<?> list) {
        contentPanel.add(new JLabel(fieldName), "gapy 5 0"); // Add the field name label
        JTextArea textArea = new JTextArea(); // Create a text area for the list
        textArea.setEditable(false); // Make the text area non-editable
        textArea.setLineWrap(true); // Enable line wrapping
        textArea.setWrapStyleWord(true); // Wrap by word
        if (list != null && !list.isEmpty()) {
            for (Object item : list) {
                textArea.append(item.toString() + "\n"); // Append each item in the list
            }
        } else {
            textArea.setText("N/A"); // Set N/A if the list is empty or null
        }
        contentPanel.add(new JScrollPane(textArea), "growx"); // Add the text area to the panel within a scroll pane
    }
}