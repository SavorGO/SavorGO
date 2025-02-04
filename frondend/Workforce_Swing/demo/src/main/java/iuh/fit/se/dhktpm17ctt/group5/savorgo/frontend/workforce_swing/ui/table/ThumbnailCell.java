package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.table;

import javax.swing.ImageIcon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A data model representing a thumbnail cell with an image URL, name, and status.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThumbnailCell {
    
    /**
     * The URL of the image to be displayed in the thumbnail.
     */
    private String imageUrl;
    
    /**
     * The name associated with the thumbnail.
     */
    private String name;
    
    /**
     * The status text associated with the thumbnail.
     */
    private String status;
    
    private ImageIcon imageIcon;
}
