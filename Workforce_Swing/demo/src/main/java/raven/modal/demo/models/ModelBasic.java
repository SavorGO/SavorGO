package raven.modal.demo.models;
import java.io.IOException;
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
	@JsonProperty("created_time")
	private LocalDateTime createdTime;
	@JsonProperty("modified_time")
	private LocalDateTime modifiedTime;
	protected abstract Object[] toTableRowBasic() throws IOException; 
}
