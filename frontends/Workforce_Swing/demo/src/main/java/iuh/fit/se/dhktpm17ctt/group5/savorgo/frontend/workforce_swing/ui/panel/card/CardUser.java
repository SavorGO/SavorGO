package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.card;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.UserController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.User;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import com.formdev.flatlaf.FlatClientProperties;
import java.io.IOException;

/**
 * Class representing a card for a user item.
 */
public class CardUser  extends CardBasic<User> {
    private UserController userController;

    public CardUser (User user) {
        super(user);
    }

    @Override
    protected JPanel createHeader() {
        userController = new UserController();
        JPanel header = new JPanel(new MigLayout("fill,insets 0", "[fill]", "[top]"));
        header.putClientProperty(FlatClientProperties.STYLE, "background:null");

        JLabel label = new JLabel();
        try {
            label.setIcon(userController.getImage(model, 130, 130, 20)); // Assuming a method to get user image
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
        addEmailLabel(body);
        addRoleLabel(body);
        addPointsLabel(body);
        addTierLabel(body);

        return body;
    }

    private void addNameLabel(JPanel body) {
        JLabel nameLabel = new JLabel(model.getFirstName() + " " + model.getLastName());
        nameLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
        body.add(nameLabel, "align center");
    }

    private void addEmailLabel(JPanel body) {
        JLabel emailLabel = new JLabel("Email: " + model.getEmail());
        emailLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
        body.add(emailLabel);
    }

    private void addRoleLabel(JPanel body) {
        JLabel roleLabel = new JLabel("Role: " + model.getRole().getDisplayName());
        roleLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
        body.add(roleLabel);
    }

    private void addPointsLabel(JPanel body) {
        JLabel pointsLabel = new JLabel("Points: " + model.getPoints());
        pointsLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
        body.add(pointsLabel);
    }

    private void addTierLabel(JPanel body) {
        JLabel tierLabel = new JLabel("Tier: " + model.getTier().getDisplayName());
        tierLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
        body.add(tierLabel);
    }


    private void addStatusLabel(JPanel body) {
        JLabel statusLabel = new JLabel(model.getStatus().getDisplayName());
        statusLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
        statusLabel.setForeground(getStatusColor(model.getStatus().getDisplayName())); // Set color based on status
        body.add(statusLabel);
    }
}