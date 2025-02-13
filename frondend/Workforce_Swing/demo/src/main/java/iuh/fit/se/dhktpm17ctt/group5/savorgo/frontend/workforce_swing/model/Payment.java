package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Payment {
    private String paymentMethod; // Phương thức thanh toán
    private double amount; // Số tiền thanh toán
    private String status; // Trạng thái thanh toán
    private LocalDateTime createdAt; // Thời gian tạo thanh toán
    private LocalDateTime updatedAt; // Thời gian cập nhật thanh toán
    private String paymentGateway; // Cổng thanh toán
    private String transactionId; // ID giao dịch
    private String paymentReference; // Tham chiếu thanh toán
}