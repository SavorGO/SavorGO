package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.TableStatusEnum;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.table.ThumbnailCell;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.CustomDateDeserializer;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.CustomDateSerializer;
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
public class Table extends ModelBasic {
	private long id;
	private String name;
	private TableStatusEnum status = TableStatusEnum.OUT_OF_SERVICE;
	
	/**
	 * @return true if reservedTime is after now
	 */
	@JsonIgnore
	public boolean isReserved() {
		//If reversedTime is after now, return true
		return reservedTime != null && this.reservedTime.isAfter(LocalDateTime.now());
	}
	
	@JsonProperty("reserved_time")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss[Z]")
    @JsonDeserialize(using = CustomDateDeserializer.class) // Dùng custom deserializer
    @JsonSerialize(using = CustomDateSerializer.class) // Dùng custom serializer
	private LocalDateTime reservedTime;
}
