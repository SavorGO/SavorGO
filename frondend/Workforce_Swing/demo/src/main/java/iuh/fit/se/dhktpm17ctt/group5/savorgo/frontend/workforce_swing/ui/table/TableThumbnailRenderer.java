package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.table;

import com.formdev.flatlaf.FlatClientProperties;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.component.MyImageIcon;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TableThumbnailRenderer extends JPanel implements TableCellRenderer {

    private final TableCellRenderer delegate;
    private JLabel labelThumbnail;
    private JLabel labelName;
    private JLabel labelStatus;

    public TableThumbnailRenderer(JTable table) {
        delegate = table.getDefaultRenderer(Object.class);
        init();
    }

    private void init() {
        setLayout(new MigLayout("ay center,insets 7 0 7 0", "", "[sg h,bottom][sg h,top]"));
        
        labelThumbnail = new JLabel();
        labelName = new JLabel();
        labelStatus = new JLabel();
        labelStatus.putClientProperty(FlatClientProperties.STYLE, "" +
                "foreground:$Label.disabledForeground;");

        add(labelThumbnail, "span 1 2,w 55::,h 55::,grow 0");
        add(labelName, "cell 1 0");
        add(labelStatus, "cell 1 1");
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel com = (JLabel) delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (value instanceof ThumbnailCell) {
            ThumbnailCell thumbnail = (ThumbnailCell) value;

            // Kiểm tra nếu ảnh đã được tải trước đó
            if (thumbnail.getImageIcon() == null) {  // Kiểm tra nếu chưa tải ảnh
                // Tạo một ExecutorService mới
                ExecutorService executor = Executors.newSingleThreadExecutor();
                final ImageIcon[] iconHolder = new MyImageIcon[1]; // Sử dụng mảng để giữ giá trị icon

                executor.submit(() -> {
                    try {
                        String imagePath = thumbnail.getImageUrl();  // Lấy đường dẫn ảnh từ thumbnail
                        
                        if ("table".equals(imagePath)) {
                            iconHolder[0] = new MyImageIcon("src/main/resources/images/png/table.png", 55, 55, 10);
                        } else {
                            iconHolder[0] = MyImageIcon.getMyImageIconFromCloudinaryImageTag(imagePath, 55, 55, 10);

                            if (iconHolder[0] == null) {
                                iconHolder[0] = new MyImageIcon("src/main/resources/images/png/no_image_found.png", 55, 55, 10);
                            }
                        }

                        // Lưu ảnh vào ThumbnailCell để tránh tải lại
                        thumbnail.setImageIcon(iconHolder[0]);

                        SwingUtilities.invokeLater(() -> {
                            labelThumbnail.setIcon(iconHolder[0]);
                            Rectangle cellRect = table.getCellRect(row, column, false);
                            table.repaint(cellRect);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        iconHolder[0] = new ImageIcon(getClass().getResource("/demo/src/main/resources/images/png/no_image_found.png"));
                        SwingUtilities.invokeLater(() -> {
                            labelThumbnail.setIcon(iconHolder[0]);
                            Rectangle cellRect = table.getCellRect(row, column, false);
                            table.repaint(cellRect);
                        });
                    }
                });
                executor.shutdown();
            } else {
                // Nếu ảnh đã được tải, chỉ cần set lại icon từ ThumbnailCell
                labelThumbnail.setIcon(thumbnail.getImageIcon());
            }

            // Set name and status
            labelName.setText(thumbnail.getName());
            String status = thumbnail.getStatus();
            labelStatus.setText(status);

            // Switch case to set color for different status
            switch (status) {
                case "Available":
                    labelStatus.setForeground(Color.GREEN);  // Tốt - Màu xanh lá
                    break;
                case "Out of Service":
                    labelStatus.setForeground(Color.RED);  // Ngừng hoạt động - Màu đỏ
                    break;
                case "Occupied":
                    labelStatus.setForeground(Color.YELLOW);  // Đang bận - Màu vàng
                    break;
                case "Needs Cleaning":
                    labelStatus.setForeground(Color.ORANGE);  // Cần dọn dẹp - Màu cam
                    break;
                case "Deleted":
                    labelStatus.setForeground(Color.BLACK);  // Đã bị xóa - Màu đen
                    break;
                case "Discontinued":
                    labelStatus.setForeground(Color.GRAY);  // Ngừng cung cấp - Màu xám
                    break;
                case "Out of Stock":
                    labelStatus.setForeground(Color.RED);  // Hết nguyên liệu - Màu đỏ
                default:
                    labelStatus.setForeground(Color.GRAY);  // Trạng thái mặc định
                    break;
            }

            // Set background and border to match table's selected state
            setBackground(com.getBackground());
            setBorder(com.getBorder());
            return this;
        }
        return com;
    }
}
