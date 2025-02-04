package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model;

import java.io.IOException;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Abstract class ModelBasic represents a basic model in the application.
 * It contains common properties for all models, including creation and modification timestamps.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class ModelBasic {
    
    /** 
     * The time when the object was created.
     * This field is mapped to the JSON property "created_time".
     */
    @JsonProperty("created_time")
    private LocalDateTime createdTime;

    /** 
     * The time when the object was last modified.
     * This field is mapped to the JSON property "modified_time".
     */
    @JsonProperty("modified_time")
    private LocalDateTime modifiedTime;

    /**
     * Abstract method to convert the object into a basic table row representation.
     * Subclasses must implement this method to provide specific conversion logic.
     * 
     * @return An array of objects representing the table row.
     * @throws IOException if an error occurs during the conversion process.
     */
    protected abstract Object[] toTableRowBasic() throws IOException; 
}