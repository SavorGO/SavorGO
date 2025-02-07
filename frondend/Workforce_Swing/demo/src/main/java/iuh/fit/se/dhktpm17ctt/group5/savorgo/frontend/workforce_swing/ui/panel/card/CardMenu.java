package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.card;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.MenuController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Menu;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.component.MyImageIcon;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import com.formdev.flatlaf.FlatClientProperties;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * Class representing a card for a menu item.
 */
public class CardMenu extends CardBasic<Menu> {
	private MenuController menuController;
    public CardMenu(Menu menu){
        super(menu);
    }

    @Override
    protected JPanel createHeader() {
    	menuController = new MenuController();
        JPanel header = new JPanel(new MigLayout("fill,insets 0", "[fill]", "[top]"));
        header.putClientProperty(FlatClientProperties.STYLE, "background:null");

        JLabel label = new JLabel();
        try {
            label.setIcon(menuController.getImage(model,130, 130, 20));
        } catch (IOException e) {
            label.setIcon(null);
        }
        header.add(label);
        return header;
    }

    @Override
    protected JPanel createBody() {
        JPanel body = new JPanel(new MigLayout("wrap, align left", "[left]", "[][][][][grow]"));
        body.putClientProperty(FlatClientProperties.STYLE, "background:null;");
        addNameLabel(body);
        addStatusLabel(body);
        addCategoryLabel(body);
        addPriceLabel(body);
        addCreateDateLabel(body);
        addUpdateDateLabel(body);

        return body;
    }


    private void addNameLabel(JPanel body) {
        JLabel nameLabel = new JLabel(model.getName());
        nameLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
        body.add(nameLabel, "align center");
    }

    private void addCategoryLabel(JPanel body) {
        JLabel categoryLabel = new JLabel("Category: " + model.getCategory().getDisplayName());
        categoryLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
        body.add(categoryLabel);
    }

    private void addStatusLabel(JPanel body) {
        JLabel statusLabel = new JLabel(model.getStatus().getDisplayName());
        statusLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
        statusLabel.setForeground(getStatusColor(model.getStatus().getDisplayName())); // Set color based on status
        body.add(statusLabel);
    }

    private void addPriceLabel(JPanel body) {
        JLabel priceLabel = new JLabel("Price: " + model.getOriginalPrice() + " -> " + model.getSalePrice());
        priceLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
        body.add(priceLabel);
    }

    private void addCreateDateLabel(JPanel body) {
        JLabel createDateLabel = new JLabel("Created At: " +
                model.getCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        createDateLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
        body.add(createDateLabel);
    }

    private void addUpdateDateLabel(JPanel body) {
        JLabel updateDateLabel = new JLabel("Updated At: " +
                model.getModifiedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        updateDateLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
        body.add(updateDateLabel);
    }
}