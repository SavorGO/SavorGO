package raven.modal.demo.models;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import raven.modal.demo.component.MyImageIcon;
import raven.modal.demo.utils.table.ThumbnailCell;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ModelMenu extends ModelBasic {
	private String id;
    private String name;
    private EnumMenuStatus status = EnumMenuStatus.DISCONTINUED;
    private EnumMenuCategory category;
    private String description;
    private double originalPrice;
    private double salePrice;
    @JsonProperty("public_id")
    private String publicId;
    private List<MenuSize> sizes;  // Danh sách các kích thước
    private List<MenuOption> options;  // Danh sách các tùy chọn
    @JsonProperty("reserved_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss[Z]")
    private LocalDateTime reservedTime;

    @Override
    @JsonIgnore
    public Object[] toTableRowBasic() throws IOException {
    	return new Object[]{
                false,
                this.getId(),
                this.getThumbnailCell(),
                (this.getCategory()==null)?"OTHER":getCategory().getDisplayName(),
                this.getOriginalPrice(),
                this.getSalePrice(),  
                this.getCreatedTime(),
                this.getModifiedTime()
        };
    }
    @JsonIgnore
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
    
    @JsonIgnore
    public ThumbnailCell getThumbnailCell() {
	    return new ThumbnailCell(this.publicId == null ? null : "SavorGO/Menus/"+this.publicId, this.getName(),this.getStatus().getDisplayName(),null);
    }
    
    @JsonIgnore
    public MyImageIcon getImage(int height, int width, int round) throws IOException {
    	if(height == 0 || width == 0) {
	    	// set default 50 round 0
	    	height = 50;
	    	width = 50;
	    	round = 0;
    	}
	    try {
			return MyImageIcon.getMyImageIconFromCloudinaryImageTag("SavorGO/Menus/"+publicId, height, width, round);
		} catch (URISyntaxException | IOException e) {
			// return no image found
			return new MyImageIcon("src/main/resources/images/system/no_image_found.png", 55, 55, 10);
		}
    }
    @Override
	public String toString() {
		return this.getName();
	}
}



