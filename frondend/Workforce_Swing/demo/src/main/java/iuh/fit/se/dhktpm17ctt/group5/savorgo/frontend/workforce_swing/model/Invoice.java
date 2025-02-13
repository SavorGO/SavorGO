package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Invoice extends ModelBasic {
    private String id; // ID của hóa đơn
    private String customerId; // ID của khách hàng
    private String staffId; // ID của nhân viên
    private String deliveryAddress; // Địa chỉ giao hàng
    private LocalDateTime orderTime; // Thời gian đặt hàng
    private List<InvoiceItem> items; // Danh sách các mặt hàng trong hóa đơn
    private List<Payment> payments; // Danh sách các phương thức thanh toán
    private int points; // Điểm thưởng
    private String additionalRequests; // Yêu cầu bổ sung
    private String status; // Trạng thái của hóa đơn
    private String totalDue; // Tổng số tiền phải trả
    private LocalDateTime createdTime; // Thời gian tạo hóa đơn
    private LocalDateTime modifiedTime; // Thời gian sửa đổi hóa đơn
}