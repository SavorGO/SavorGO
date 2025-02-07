package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.info;

import com.formdev.flatlaf.FlatClientProperties;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.MenuController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.PromotionController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.PromotionDiscountTypeEnum;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Menu;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Promotion;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.PopupFormBasic;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.DefaultComponent;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

import java.awt.MenuContainer;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class PromotionInfoForm extends PopupFormBasic<Promotion> {
    private MenuController menuController;
    private PromotionController promotionController;
    private Promotion promotion;

    public PromotionInfoForm(long id) throws IOException {
    	menuController = new MenuController();
    	promotionController = new PromotionController();
        promotion = promotionController.getPromotionById(id); // Assuming you have a method to get promotion by id
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
        Menu menu = menuController.getMenuById(promotion.getMenuId());
        // image
		if (menuController.getThumbnailCell(menu) != null) {
			contentPanel.add(DefaultComponent.createThumbnailPanel(menuController.getThumbnailCell(menu), true), "gapy 5 0");
		}

        // Add fields to display information
        addField("Promotion ID:", promotion.getId()+"");
        addField("Promotion Name:", promotion.getName());
        addField("Menu Name:", menu.getName());
        addField("Discount Type:", promotion.getPromotionDiscountTypeEnum().toString());
        
        // Calculate discount values
        String discountValue = null;
        String discountedValue = null;
        if (promotion.getPromotionDiscountTypeEnum() == PromotionDiscountTypeEnum.PERCENT) {
            discountValue = promotion.getDiscountValue() + "%";
            discountedValue = menu.getSalePrice() - (menu.getSalePrice() * promotion.getDiscountValue() / 100) + "";
        } else if (promotion.getPromotionDiscountTypeEnum() == PromotionDiscountTypeEnum.FLAT) {
            discountValue = promotion.getDiscountValue() + "";
            discountedValue = menu.getSalePrice() - promotion.getDiscountValue() + "";
        }
        addField("Discount Value:", discountValue);
        addField("Discounted Price:", discountedValue);
        if(promotion.getStartDate() != null)
        addField("Start Date:", promotion.getStartDate().toString());
		if(promotion.getEndDate() != null)
        addField("End Date:", promotion.getEndDate().toString());
        addField("Created At:", promotion.getCreatedTime().toString());
        addField("Updated At:", promotion.getModifiedTime().toString());
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
