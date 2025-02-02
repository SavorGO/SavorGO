package raven.modal.demo.forms.cards;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.modal.demo.component.MyImageIcon;
import raven.modal.demo.forms.other.CardBasic;
import raven.modal.demo.models.ModelTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class CardTable extends CardBasic<ModelTable> {
    private boolean selected = false;
    private ModelTable modelTable;

    public boolean isSelected() {
        return selected;    
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public ModelTable getModelTable() {
        return modelTable;
    }

    public CardTable(ModelTable modelTable, Consumer<ModelTable> event) {
        super(modelTable, event);
        this.modelTable = modelTable;
        init();
    }

    @Override
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
    private void setupCardStyle() {
        putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:30;" +
                "[light]background:darken($Panel.background,3%);" +
                "[dark]background:lighten($Panel.background,3%);");
    }

    /** 
     * Create the header panel of the card.
     * @return JPanel representing the header.
     */
    @Override
    protected JPanel createHeader() {
        JPanel header = new JPanel(new MigLayout("fill,insets 0", "[fill]", "[top]"));
        header.putClientProperty(FlatClientProperties.STYLE, "background:null");
        
        JLabel label = new JLabel();
        try {
			label.setIcon(new MyImageIcon("src/main/resources/images/system/table.png", 130, 130, 20));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        header.add(label);
        return header;
    }

    /** 
     * Create the body panel of the card.
     * @return JPanel representing the body.
     */
    @Override
    protected JPanel createBody() {
        JPanel body = new JPanel(new MigLayout("wrap, align center", "[center]", "[][][][][grow]"));
        body.putClientProperty(FlatClientProperties.STYLE, "background:null;");
        
        addTableName(body);
        addTableStatus(body);
        addReservationInfo(body);
        addCreationDate(body);
        addUpdateDate(body);
        
        return body;
    }

    /** 
     * Add the table name to the body panel.
     * @param body The body panel to which the name will be added.
     */
    private void addTableName(JPanel body) {
        JLabel nameLabel = new JLabel(modelBasic.getName());
        nameLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
        body.add(nameLabel, "align center");
    }

    /** 
     * Add the table status to the body panel.
     * @param body The body panel to which the status will be added.
     */
    private void addTableStatus(JPanel body) {
        JLabel statusLabel = new JLabel(modelBasic.getStatus().getDisplayName());
        statusLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
        statusLabel.setForeground(getStatusColor(modelBasic.getStatus().getDisplayName()));
        body.add(statusLabel);
    }

    /** 
     * Add reservation information to the body panel.
     * @param body The body panel to which the reservation info will be added.
     */
    private void addReservationInfo(JPanel body) {
        JLabel reservedLabel = new JLabel("Is Reserved: " + (modelBasic.isReserved() ? "Yes" : "No"));
        reservedLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
        body.add(reservedLabel);
    }

    /** 
     * Add creation date to the body panel.
     * @param body The body panel to which the creation date will be added.
     */
    private void addCreationDate(JPanel body) {
        JLabel createDateLabel = new JLabel("Created At: " +
                modelBasic.getCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        createDateLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
        body.add(createDateLabel);
    }

    /** 
     * Add update date to the body panel.
     * @param body The body panel to which the update date will be added.
     */
    private void addUpdateDate(JPanel body) {
        JLabel updateDateLabel = new JLabel("Updated At: " +
                modelBasic.getModifiedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        updateDateLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
        body.add(updateDateLabel);
    }

    /** 
     * Get the color based on the table status.
     * @param status The status of the table.
     * @return Color corresponding to the status.
     */
    private Color getStatusColor(String status) {
        switch (status) {
            case "Available":
                return new Color(0, 128, 0); // Green (available)
            case "Out of Service":
                return new Color(255, 0, 0); // Red (out of service)
            case "Occupied":
                return new Color(255, 165, 0); // Orange (occupied)
            case "Needs Cleaning":
                return new Color(0, 0, 255); // Blue (needs cleaning)
            case "Deleted":
                return new Color(128, 128, 128); // Gray (deleted)
            default:
                return Color.BLACK; // Default is black
        }
    }
}