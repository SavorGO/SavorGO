package raven.modal.demo.models;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import raven.modal.demo.utils.CustomDateDeserializer;
import raven.modal.demo.utils.CustomDateSerializer;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ModelTable extends ModelBasic {
	private String name;
	private EnumTableStatus status;
	
	/**
	 * @return true if reservedTime is after now
	 */
	public boolean isReserved() {
		//If reversedTime is after now, return true
		return reservedTime != null && this.reservedTime.isAfter(LocalDateTime.now());
	}
	
	@JsonProperty("reserved_time")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss[Z]")
    @JsonDeserialize(using = CustomDateDeserializer.class) // Dùng custom deserializer
    @JsonSerialize(using = CustomDateSerializer.class) // Dùng custom serializer
	private LocalDateTime reservedTime;
	@Override
	public Object[] toTableRowBasic() {
		return new Object[]{ false, this.getId(), this.getName(), this.getStatus(), this.isReserved(), this.getReservedTime(), this.getCreatedTime(), this.getModifiedTime()};
	}
	//fullconstructor: id, name, status, isReserved, reservedTime, createdTime, modifiedTime
}
