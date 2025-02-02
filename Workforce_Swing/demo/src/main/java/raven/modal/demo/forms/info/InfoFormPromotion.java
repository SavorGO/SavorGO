package raven.modal.demo.forms.info;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.modal.demo.controllers.ControllerMenu;
import raven.modal.demo.controllers.ControllerPromotion;
import raven.modal.demo.models.ModelMenu;
import raven.modal.demo.models.ModelPromotion;
import raven.modal.demo.utils.DefaultComponent;
import raven.modal.demo.models.EnumDiscountType;

import javax.swing.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class InfoFormPromotion extends PopupFormBasic<ModelPromotion> {
    private ControllerMenu controllerMenu;
    private ControllerPromotion controllerPromotion;
    private ModelPromotion modelPromotion;

    public InfoFormPromotion(long id) throws IOException {
        controllerMenu = new ControllerMenu();
        controllerPromotion = new ControllerPromotion();
        modelPromotion = controllerPromotion.getPromotionById(id); // Assuming you have a method to get promotion by id
        init();
    }

    @Override
    protected void init() throws IOException {
        createTitle(); // Create the title and add it to the content panel
        createFields(); // Create fields and add them to the content panel
        setViewportView(contentPanel); // Set the content panel to the scroll pane
    }

    /** 
     * Create the title label for the form.
     */
    @Override
    protected void createTitle() {
        JLabel lb = new JLabel("Promotion Information");
        lb.putClientProperty(FlatClientProperties.STYLE, "font:+2");
        contentPanel.add(lb, "gapy 5 0"); // Add title label to the panel
        contentPanel.add(new JSeparator(), "height 2!,gapy 0 0"); // Add a separator below the title
    }

    /** 
     * Create fields to display the promotion information.
     * @throws IOException 
     */
    @Override
    protected void createFields() throws IOException {
        // Get the associated menu
        ModelMenu menu = controllerMenu.getMenuById(modelPromotion.getMenuId());
        // image
		if (menu.getThumbnailCell() != null) {
			contentPanel.add(DefaultComponent.createThumbnailPanel(menu.getThumbnailCell(), true), "gapy 5 0");
		}

        // Add fields to display information
        addField("Promotion ID:", modelPromotion.getId()+"");
        addField("Promotion Name:", modelPromotion.getName());
        addField("Menu Name:", menu.getName());
        addField("Discount Type:", modelPromotion.getDiscountType().toString());
        
        // Calculate discount values
        String discountValue = null;
        String discountedValue = null;
        if (modelPromotion.getDiscountType() == EnumDiscountType.PERCENT) {
            discountValue = modelPromotion.getDiscountValue() + "%";
            discountedValue = menu.getSalePrice() - (menu.getSalePrice() * modelPromotion.getDiscountValue() / 100) + "";
        } else if (modelPromotion.getDiscountType() == EnumDiscountType.FLAT) {
            discountValue = modelPromotion.getDiscountValue() + "";
            discountedValue = menu.getSalePrice() - modelPromotion.getDiscountValue() + "";
        }
        addField("Discount Value:", discountValue);
        addField("Discounted Price:", discountedValue);
        if(modelPromotion.getStartDate() != null)
        addField("Start Date:", modelPromotion.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		if(modelPromotion.getEndDate() != null)
        addField("End Date:", modelPromotion.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        addField("Created At:", modelPromotion.getCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        addField("Updated At:", modelPromotion.getModifiedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
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
}
