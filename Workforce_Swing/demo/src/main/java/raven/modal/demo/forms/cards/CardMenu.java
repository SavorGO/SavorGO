package raven.modal.demo.forms.card;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.extras.AvatarIcon;
import raven.modal.demo.component.MyImageIcon;
import raven.modal.demo.models.ModelMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class CardMenu extends CardBasic<ModelMenu> {
    private boolean selected = false;
    private ModelMenu modelMenu;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public ModelMenu getModelMenu() {
        return modelMenu;
    }

    public CardMenu(ModelMenu modelMenu, Consumer<ModelMenu> event) {
        super(modelMenu, event);
        this.modelMenu = modelMenu;
        init();
    }

    @Override
    protected void init() {
        putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:30;" +
                "[light]background:darken($Panel.background,3%);" +
                "[dark]background:lighten($Panel.background,3%);");

        setLayout(new MigLayout("", "fill"));
        panelHeader = createHeader();
        panelBody = createBody();
        add(panelHeader);
        add(panelBody);
    }

    @Override
    protected JPanel createHeader() {
        JPanel header = new JPanel(new MigLayout("fill,insets 0", "[fill]", "[top]"));
        header.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null");

        JLabel label = new JLabel();
        label.setIcon(new MyImageIcon("src/main/resources/raven/modal/demo/icons/menu.png", 130, 130, 20));
        header.add(label);
        return header;
    }

    @Override
    protected JPanel createBody() {
        JPanel body = new JPanel(new MigLayout("wrap, align center", "[center]", "[][][][][grow]"));
        body.putClientProperty(FlatClientProperties.STYLE, "background:null;");
        
        addNameLabel(body);
        addCategoryLabel(body);
        addStatusLabel(body);
        addPriceLabel(body);
        addCreateDateLabel(body);
        addUpdateDateLabel(body);

        return body;
    }

    /**
     * Add the menu name label to the body panel.
     */
    private void addNameLabel(JPanel body) {
        JLabel nameLabel = new JLabel(modelMenu.getName());
        nameLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
        body.add(nameLabel, "align center");
    }

    /**
     * Add the category label to the body panel.
     */
    private void addCategoryLabel(JPanel body) {
        JLabel categoryLabel = new JLabel("Category: " + modelMenu.getCategory());
        categoryLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
        body.add(categoryLabel);
    }

    /**
     * Add the status label to the body panel.
     */
    private void addStatusLabel(JPanel body) {
        JLabel statusLabel = new JLabel(modelMenu.getStatus());
        statusLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
        statusLabel.setForeground(getStatusColor(modelMenu.getStatus())); // Set color based on status
        body.add(statusLabel);
    }

    /**
     * Add the price label to the body panel.
     */
    private void addPriceLabel(JPanel body) {
        JLabel priceLabel = new JLabel("Price: " + modelMenu.getOriginalPrice() + " -> " + modelMenu.getSalePrice());
        priceLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
        body.add(priceLabel);
    }

    /**
     * Add the creation date label to the body panel.
     */
    private void addCreateDateLabel(JPanel body) {
        JLabel createDateLabel = new JLabel("Created At: " +
                modelMenu.getCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        createDateLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
        body.add(createDateLabel);
    }

    /**
     * Add the update date label to the body panel.
     */
    private void addUpdateDateLabel(JPanel body) {
        JLabel updateDateLabel = new JLabel("Updated At: " +
                modelMenu.getModifiedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        updateDateLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
        body.add(updateDateLabel);
    }


    /**
     * Get color based on the status
     */
    private Color getStatusColor(String status) {
        switch (status) {
            case "Available":
                return new Color(0, 128, 0); // Green (available)
            case "Out of Stock":
                return new Color(255, 0, 0); // Red (out of stock)
            case "Sold":
                return new Color(255, 165, 0); // Orange (sold)
            case "Deleted":
                return new Color(128, 128, 128); // Gray (deleted)
            default:
                return Color.BLACK; // Default is black
        }
    }
}
