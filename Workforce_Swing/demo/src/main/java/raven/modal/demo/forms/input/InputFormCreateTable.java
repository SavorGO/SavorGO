package raven.modal.demo.forms.input;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.modal.demo.models.ModelTable;

import javax.swing.*;

public class InputFormCreateTable extends InputFormBasic<ModelTable> {

    private JTextField txtName;  // Khai báo txtName để truy cập giá trị bên ngoài

    public InputFormCreateTable() {
        super();
    }

    @Override
    protected void init() {
        setLayout(new MigLayout("fillx,wrap,insets 5 30 5 30,width 400", "[fill]", ""));
        createTitle();
        // Define fields
        txtName = new JTextField();

        // Add placeholder text
        txtName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Table Name");

        add(new JLabel("Table Name"), "gapy 5 0");
        add(txtName);
    }

    @Override
    protected void createTitle() {
        JLabel lb = new JLabel("Table Information");
        lb.putClientProperty(FlatClientProperties.STYLE, "font:+2");
        add(lb, "gapy 5 0");
        add(new JSeparator(), "height 2!,gapy 0 0");
    }

    // Getter method to get the name entered by the user
    public String getTableName() {
        return txtName.getText();
    }
}
