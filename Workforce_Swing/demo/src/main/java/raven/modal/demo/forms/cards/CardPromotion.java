package raven.modal.demo.forms.cards;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.modal.demo.component.MyImageIcon;
import raven.modal.demo.forms.other.CardBasic;
import raven.modal.demo.models.ModelPromotion;
import raven.modal.demo.models.EnumStatusPromotion;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class CardPromotion extends CardBasic<ModelPromotion> {
    private boolean selected = false;
    private ModelPromotion modelPromotion;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public ModelPromotion getModelPromotion() {
        return modelPromotion;
    }

    public CardPromotion(ModelPromotion modelPromotion, Consumer<ModelPromotion> event) {
        super(modelPromotion, event);
        this.modelPromotion = modelPromotion;
        init();
    }

    @Override
    protected void init() {
        setupCardStyle();
        setLayout(new MigLayout("", "fill"));
        JPanel panelHeader = createHeader();
        JPanel panelBody = createBody();
        add(panelHeader);
        add(panelBody);
    }

    /**
     * Cài đặt kiểu cho card (ví dụ: bo tròn, nền thay đổi theo chế độ Light/Dark).
     */
    private void setupCardStyle() {
        putClientProperty(FlatClientProperties.STYLE, 
                "arc:30;" +
                "[light]background:darken($Panel.background,3%);" +
                "[dark]background:lighten($Panel.background,3%);");
    }

    @Override
    protected JPanel createHeader() {
        JPanel header = new JPanel(new MigLayout("fill,insets 0", "[fill]", "[top]"));
        header.putClientProperty(FlatClientProperties.STYLE, "background:null");

        JLabel label = new JLabel();
        try {
            // Đảm bảo bạn có icon promotion tại đường dẫn chỉ định
            label.setIcon(new MyImageIcon("src/main/resources/raven/modal/demo/icons/promotion.png", 130, 130, 20));
        } catch (IOException e) {
            label.setIcon(null);
        }
        header.add(label);
        return header;
    }

    @Override
    protected JPanel createBody() {
        // Cấu hình layout cho body. Số dòng và cột có thể điều chỉnh theo nhu cầu
        JPanel body = new JPanel(new MigLayout("wrap, align center", "[center]", "[][][][][][][][][grow]"));
        body.putClientProperty(FlatClientProperties.STYLE, "background:null;");

        addNameLabel(body);
        addDiscountValueLabel(body);
        addDiscountTypeLabel(body);
        addMenuIdLabel(body);
        addStartDateLabel(body);
        addEndDateLabel(body);
        addStatusLabel(body);
        addCreatedDateLabel(body);
        addModifiedDateLabel(body);

        return body;
    }

    /**
     * Thêm label hiển thị tên khuyến mãi.
     */
    private void addNameLabel(JPanel body) {
        JLabel nameLabel = new JLabel(modelPromotion.getName());
        nameLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
        body.add(nameLabel, "align center");
    }

    /**
     * Thêm label hiển thị giá trị giảm giá.
     */
    private void addDiscountValueLabel(JPanel body) {
        JLabel discountValueLabel = new JLabel("Discount: " + modelPromotion.getDiscountValue());
        discountValueLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
        body.add(discountValueLabel);
    }

    /**
     * Thêm label hiển thị loại giảm giá (PERCENT hoặc FLAT).
     */
    private void addDiscountTypeLabel(JPanel body) {
        JLabel discountTypeLabel = new JLabel("Type: " + modelPromotion.getDiscountType().toString());
        discountTypeLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
        body.add(discountTypeLabel);
    }

    /**
     * Thêm label hiển thị ID của menu liên quan.
     */
    private void addMenuIdLabel(JPanel body) {
        JLabel menuIdLabel = new JLabel("Menu ID: " + modelPromotion.getMenuId());
        menuIdLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
        body.add(menuIdLabel);
    }

    /**
     * Thêm label hiển thị ngày bắt đầu khuyến mãi.
     */
    private void addStartDateLabel(JPanel body) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        JLabel startDateLabel = new JLabel("Start Date: " + modelPromotion.getStartDate().format(formatter));
        startDateLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
        body.add(startDateLabel);
    }

    /**
     * Thêm label hiển thị ngày kết thúc khuyến mãi.
     */
    private void addEndDateLabel(JPanel body) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        JLabel endDateLabel = new JLabel("End Date: " + modelPromotion.getEndDate().format(formatter));
        endDateLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
        body.add(endDateLabel);
    }

    /**
     * Thêm label hiển thị trạng thái khuyến mãi.
     */
    private void addStatusLabel(JPanel body) {
        JLabel statusLabel = new JLabel(modelPromotion.getStatus().toString());
        statusLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
        statusLabel.setForeground(getStatusColor(modelPromotion.getStatus()));
        body.add(statusLabel);
    }

    /**
     * Thêm label hiển thị thời gian tạo.
     */
    private void addCreatedDateLabel(JPanel body) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        JLabel createdLabel = new JLabel("Created At: " + modelPromotion.getCreatedTime().format(formatter));
        createdLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
        body.add(createdLabel);
    }

    /**
     * Thêm label hiển thị thời gian chỉnh sửa.
     */
    private void addModifiedDateLabel(JPanel body) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        JLabel modifiedLabel = new JLabel("Modified At: " + modelPromotion.getModifiedTime().format(formatter));
        modifiedLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
        body.add(modifiedLabel);
    }

    /**
     * Xác định màu cho label trạng thái dựa theo EnumStatusPromotion.
     */
    private Color getStatusColor(EnumStatusPromotion status) {
        switch (status) {
            case AVAILABLE:
                return new Color(0, 128, 0); // Xanh lá (còn hiệu lực)
            case ENDED:
                return new Color(255, 165, 0); // Cam (hết hạn)
            case DELETED:
                return new Color(128, 128, 128); // Xám (đã xóa)
            default:
                return Color.BLACK;
        }
    }
}
