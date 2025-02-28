package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.info;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.formdev.flatlaf.FlatClientProperties;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.MenuController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.TableController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Menu;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Table;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.PopupFormBasic;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.ApiResponse;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.DefaultComponent;
import net.miginfocom.swing.MigLayout;
import raven.modal.Toast;

import javax.swing.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MenuInfoForm extends PopupFormBasic {
    private MenuController menuController;
    private Menu menu;
    
    public MenuInfoForm(String id) throws IOException {
        menuController = new MenuController();
        ApiResponse apiResponse = menuController.getMenuById(id);

        if (apiResponse.getErrors() != null || apiResponse.getData() == null) {
            String errorMessage = apiResponse.getErrors() != null ? apiResponse.getErrors().toString() : "Unknown error";
            Toast.show(this, Toast.Type.ERROR, "Failed to fetch menu: " + errorMessage);
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Parse `data` từ API response thành đối tượng `Menu`
        String jsonData = objectMapper.writeValueAsString(apiResponse.getData());
        menu = objectMapper.readValue(jsonData, Menu.class);

        init();
    }


    @Override
    protected void init() {
        createTitle(); // Create the title and add it to the content panel
        createFields(); // Create fields and add them to the content panel
        setViewportView(contentPanel); // Set the content panel to the scroll pane
    }
    @Override
    protected void createTitle() {
        JLabel lb = new JLabel("Menu Information");
        lb.putClientProperty(FlatClientProperties.STYLE, "font:+2");
        contentPanel.add(lb, "gapy 5 0"); // Add title label to the panel
        contentPanel.add(new JSeparator(), "height 2!,gapy 0 0"); // Add a separator below the title
    }
    
    @Override
    protected void createFields() {
    	if (menuController.getThumbnailCell(menu) != null) {
			contentPanel.add(DefaultComponent.createThumbnailPanel(menuController.getThumbnailCell(menu), true), "gapy 5 0");
		}
        addField("Menu ID:", menu.getId());
        addField("Menu Name:", menu.getName());
        addField("Category:", menu.getCategory().getDisplayName());
        addField("Description:", menu.getDescription());
        addField("Original Price:", String.valueOf(menu.getOriginalPrice()));
        addField("Sale Price:", String.valueOf(menu.getSalePrice()));
        addField("Status:", menu.getStatus().getDisplayName());
        addListField("Sizes:", menu.getSizes());
        addListField("Options:", menu.getOptions());
        addField("Created At:", menu.getCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        addField("Updated At:", menu.getModifiedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    private void addField(String fieldName, String fieldValue) {
        contentPanel.add(new JLabel(fieldName), "gapy 5 0"); // Add the field name label
        JTextField textField = new JTextField(fieldValue); // Create a text field with the field value
        textField.setEditable(false); // Make the text field non-editable
        textField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, fieldName); // Set placeholder text
        contentPanel.add(textField); // Add the text field to the panel
    }

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
