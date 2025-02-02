package raven.modal.demo.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import raven.modal.demo.utils.table.ThumbnailCell;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ModelPromotion extends ModelBasic{
    private long id; // ID tự động tăng
    private String name; // Tên khuyến mãi

    @JsonProperty("discount_value")
    private double discountValue; // Giá trị giảm giá

    @JsonProperty("discount_type")
    private EnumDiscountType discountType; // Loại giảm giá (PERCENT hoặc FLAT)

    @JsonProperty("menu_id")
    private String menuId; // ID của menu

    @JsonProperty("start_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate; // Ngày bắt đầu

    @JsonProperty("end_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate; // Ngày kết thúc

    private EnumStatusPromotion status; // Trạng thái (AVAILABLE, ENDED, DELETED)
	@Override
	protected Object[] toTableRowBasic() {
		// TODO Auto-generated method stub
		return null;
	}
}