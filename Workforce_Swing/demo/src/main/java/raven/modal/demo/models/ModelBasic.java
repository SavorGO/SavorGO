package raven.modal.demo.models;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class ModelBasic {
	private long id;
	@JsonProperty("created_time")
	private LocalDateTime createdTime;
	@JsonProperty("modified_time")
	private LocalDateTime modifiedTime;
	protected abstract Object[] toTableRowBasic(); 
}
