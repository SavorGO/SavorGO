package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.MenuCategoryEnum;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.MenuStatusEnum;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.component.MyImageIcon;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.table.ThumbnailCell;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Menu extends ModelBasic {
	private String id;
    private String name;
    private MenuStatusEnum status = MenuStatusEnum.DISCONTINUED;
    private MenuCategoryEnum category;
    private String description;
	@JsonProperty(value = "original_price")
    private double originalPrice;
	@JsonProperty(value = "sale_price")
    private double salePrice;
    @JsonProperty(value = "discounted_price")
    private double discountedPrice;
    @JsonProperty("public_id")
    private String publicId;
    private List<MenuSize> sizes;  // Danh sách các kích thước
    private List<MenuOption> options;  // Danh sách các tùy chọn
    @JsonProperty("reserved_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss[Z]")
    private LocalDateTime reservedTime;

    @Override
	public String toString() {
		return this.getName();
	}
}



