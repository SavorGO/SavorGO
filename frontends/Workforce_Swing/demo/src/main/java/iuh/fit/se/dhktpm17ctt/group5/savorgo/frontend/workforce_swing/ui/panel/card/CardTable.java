package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.card;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Table;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.component.MyImageIcon;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import com.formdev.flatlaf.FlatClientProperties;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * Class representing a card for a table item.
 */
public class CardTable extends CardBasic<Table> {

    public CardTable(Table modelTable){
        super(modelTable);
    }

    @Override
    protected JPanel createHeader() {
        JPanel header = new JPanel(new MigLayout("fill,insets 0", "[fill]", "[top]"));
        header.putClientProperty(FlatClientProperties.STYLE, "background:null");

        JLabel label = new JLabel();
        try {
            label.setIcon(new MyImageIcon("src/main/resources/images/png/table.png", 130,  130, 20));
        } catch (IOException e) {
            e.printStackTrace();
        }
        header.add(label);
        return header;
    }

    @Override
    protected JPanel createBody() {
        JPanel body = new JPanel(new MigLayout("wrap, align left", "[left]", "[][][][][grow]"));
        body.putClientProperty(FlatClientProperties.STYLE, "background:null;");

        addTableName(body);
        addTableStatus(body);
        addReservationInfo(body);
        addCreationDate(body);
        addUpdateDate(body);

        return body;
    }

    private void addTableName(JPanel body) {
        JLabel nameLabel = new JLabel(model.getName());
        nameLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
        body.add(nameLabel, "align center");
    }

    private void addTableStatus(JPanel body) {
        JLabel statusLabel = new JLabel(model.getStatus().getDisplayName());
        statusLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
        statusLabel.setForeground(getStatusColor(model.getStatus().getDisplayName()));
        body.add(statusLabel);
    }

    private void addReservationInfo(JPanel body) {
        JLabel reservedLabel = new JLabel("Is Reserved: " + (model.isReserved() ? "Yes" : "No"));
        reservedLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
        body.add(reservedLabel);
    }

    private void addCreationDate(JPanel body) {
        JLabel createDateLabel = new JLabel("Created At: " +
                model.getCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        createDateLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
        body.add(createDateLabel);
    }

    private void addUpdateDate(JPanel body) {
        JLabel updateDateLabel = new JLabel("Updated At: " +
                model.getModifiedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        updateDateLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
        body.add(updateDateLabel);
    }
}