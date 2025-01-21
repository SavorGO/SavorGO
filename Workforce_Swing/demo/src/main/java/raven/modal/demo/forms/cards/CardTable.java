package raven.modal.demo.forms.other;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.extras.AvatarIcon;
import raven.modal.demo.component.MyImageIcon;
import raven.modal.demo.models.EnumTableStatus;
import raven.modal.demo.models.ModelTable;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class CardTable extends CardBasic<ModelTable> {
    public CardTable(ModelTable ModelTable, Consumer<ModelTable> event) {
        super(ModelTable, event);
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
        		label.setIcon(new MyImageIcon("src/main/resources/raven/modal/demo/icons/table.png", 130, 130, 20));
        header.add(label);
        return header;
    }

    @Override
    protected JPanel createBody() {
        JPanel body = new JPanel(new MigLayout("wrap, align center", "[center]", "[][][][][grow]"));
        body.putClientProperty(FlatClientProperties.STYLE, "background:null;");

        // Hiển thị tên bàn
        JLabel nameLabel = new JLabel(modelBasic.getName());
        nameLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
        body.add(nameLabel, "align center");
        // Hiển thị trạng thái bàn
        JLabel statusLabel = new JLabel(modelBasic.getStatus().getDisplayName());
        statusLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
        statusLabel.setForeground(getStatusColor(modelBasic.getStatus().getDisplayName())); // Gán màu theo trạng thái
        body.add(statusLabel);

        // Hiển thị thông tin đã đặt hay chưa
        JLabel reservedLabel = new JLabel("Is Reserved: " + (modelBasic.isReserved() ? "Yes" : "No"));
        reservedLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
        body.add(reservedLabel);

        // Hiển thị thời gian tạo
        JLabel createDateLabel = new JLabel("Created At: " +
        		modelBasic.getCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        createDateLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
        body.add(createDateLabel);

        // Hiển thị thời gian cập nhật
        JLabel updateDateLabel = new JLabel("Updated At: " +
        		modelBasic.getModifiedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        updateDateLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
        body.add(updateDateLabel);

        // Nút "View"
        JButton button = new JButton("View Details");
        button.addActionListener(e -> event.accept(modelBasic));
        button.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:999;" +
                "margin:3,25,3,25;" +
                "borderWidth:1;" +
                "focusWidth:0;" +
                "innerFocusWidth:0;" +
                "background:null;");
        body.add(button);

        return body;
    }

    /**
     * Lấy màu sắc dựa trên trạng thái
     */
    private Color getStatusColor(String status) {
        switch (status) {
            case "Available":
                return new Color(0, 128, 0); // Xanh lá cây (sẵn sàng)
            case "Out of Service":
                return new Color(255, 0, 0); // Đỏ (hỏng hóc)
            case "Occupied":
                return new Color(255, 165, 0); // Cam (đang sử dụng)
            case "Needs Cleaning":
                return new Color(0, 0, 255); // Xanh dương (cần dọn dẹp)
            case "Deleted":
                return new Color(128, 128, 128); // Xám (đã xóa)
            default:
                return Color.BLACK; // Mặc định là đen
        }
    }
}
