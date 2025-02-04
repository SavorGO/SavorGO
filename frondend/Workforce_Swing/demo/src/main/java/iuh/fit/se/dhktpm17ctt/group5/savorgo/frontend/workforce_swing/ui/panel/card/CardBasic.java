package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.card;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * Abstract class representing a basic card structure.
 * This class provides common functionality for all card types.
 */
public abstract class CardBasic<ModelBasic> extends JPanel {
    protected ModelBasic model; // The model associated with the card
    protected JPanel panelHeader; // Header panel
    protected JPanel panelBody; // Body panel
    protected boolean isSelected = false;
    
    public CardBasic(ModelBasic model) {
        this.model = model;
        init();
    }
    
	public boolean isSelected() {
		return isSelected;
	}
	
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	public ModelBasic getModel() {
		return model;
	}

    /**
     * Initialize the card by setting up the style and layout.
     */
    protected void init() {
        setupCardStyle();
        setLayout(new MigLayout("", "fill"));
        panelHeader = createHeader();
        panelBody = createBody();
        add(panelHeader);
        add(panelBody);
    }

    /**
     * Set up the style for the card.
     */
    protected void setupCardStyle() {
        putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:30;" +
                "[light]background:darken($Panel.background,3%);" +
                "[dark]background:lighten($Panel.background,3%);");
    }

    /**
     * Create the header panel of the card.
     * @return JPanel representing the header.
     */
    protected abstract JPanel createHeader();

    /**
     * Create the body panel of the card.
     * @return JPanel representing the body.
     */
    protected abstract JPanel createBody();

    /**
     * Get color based on the status.
     * @param status The status string.
     * @return Color corresponding to the status.
     */
    protected Color getStatusColor(String status) {
        switch (status) {
            case "Available":
                return new Color(0, 128, 0); // Green (available)
            case "Out of Stock":
                return new Color(255, 0, 0); // Red (out of stock)
            case "Sold":
                return new Color(255, 165, 0); // Orange (sold)
            case "Deleted":
                return new Color(128, 128, 128); // Gray (deleted)
            case "Out of Service":
                return new Color(255, 0, 0); // Red (out of service)
            case "Occupied":
                return new Color(255, 165, 0); // Orange (occupied)
            case "Needs Cleaning":
                return new Color(0, 0, 255); // Blue (needs cleaning)
            case "Ended":
				return new Color(255, 165, 0); // Orange (ended)
            default:
                return Color.BLACK; // Default is black
        }
    }
}