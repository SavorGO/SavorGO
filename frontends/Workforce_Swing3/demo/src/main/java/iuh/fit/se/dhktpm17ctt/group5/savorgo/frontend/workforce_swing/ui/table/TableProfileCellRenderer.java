package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.table;

import com.formdev.flatlaf.FlatClientProperties;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.ModelProfile;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class TableProfileCellRenderer extends JPanel implements TableCellRenderer {

    private final TableCellRenderer delegate;


    public TableProfileCellRenderer(JTable table) {
        delegate = table.getDefaultRenderer(Object.class);
        init();
    }

    private void init() {
        setLayout(new MigLayout("ay center,insets 7 0 7 0", "", "[sg h,bottom][sg h,top]"));
        labelProfile = new JLabel();
        labelName = new JLabel();
        labelLocation = new JLabel();
        labelLocation.putClientProperty(FlatClientProperties.STYLE, "" +
                "foreground:$Label.disabledForeground;");

        add(labelProfile, "span 1 2,w 55::,h 55::,grow 0");
        add(labelName, "cell 1 0");
        add(labelLocation, "cell 1 1");
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel com = (JLabel) delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        System.out.println(false);
        if (value instanceof ModelProfile) {
        	System.out.println(true);
            ModelProfile profile = (ModelProfile) value;
            if (profile.getIcon() != null) {
                labelProfile.setIcon(profile.getIcon());
            } else {
                labelProfile.setIcon(null);
            }
            labelName.setText(profile.getName());
            labelLocation.setText(profile.getLocation());
            setBackground(com.getBackground());
            setBorder(com.getBorder());
            return this;
        }
        return com;
    }

    private JLabel labelProfile;
    private JLabel labelName;
    private JLabel labelLocation;
}
