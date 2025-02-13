package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InvoiceItem {
    private String id; // ID của mặt hàng
    private MenuSize size; // Kích thước của mặt hàng
    private double price; // Giá của mặt hàng
    private int quantity; // Số lượng
    private List<MenuOption> options; // Các tùy chọn cho mặt hàng
    private double totalPrice; // Tổng giá của mặt hàng
    private int promotionId; // ID khuyến mãi (nếu có)
}