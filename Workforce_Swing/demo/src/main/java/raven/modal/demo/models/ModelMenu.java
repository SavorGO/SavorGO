package raven.modal.demo.models;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class ModelMenu extends ModelBasic {
	private String id;
    private String name;
    private String category;
    private String description;
    private double originalPrice;
    private double salePrice;
    private String imageUrl;
    private String status;
    private List<Size> sizes;  // Danh sách các kích thước
    private List<Option> options;  // Danh sách các tùy chọn
    
    public String getId() {
        return this.id;
    }
    @JsonProperty("reserved_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss[Z]")
    private LocalDateTime reservedTime;

    @Override
    public Object[] toTableRowBasic() {
    	return new Object[]{
                false,
                this.getId(),
                this.getName(),
                this.getStatus(),
                this.getCategory(),
                this.getOriginalPrice(),
                this.getSalePrice(),  
                this.getCreatedTime(),
                this.getModifiedTime()
        };
    }
    
    public Object[] getDetailsInfo() {
    	return new Object[]{
                false,
                this.getId(),
                this.getName(),
                this.getCategory(),
                this.getOriginalPrice(),
                this.getSalePrice(),
                this.getStatus(),
                this.getSizes(),
                this.getOptions(),
                this.getDescription(),
                this.getCreatedTime(),
                this.getModifiedTime()
        };
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Size {
    private String sizeName;
    private double priceChange;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Option {
    private String optionName;
    private double priceChange;
}
