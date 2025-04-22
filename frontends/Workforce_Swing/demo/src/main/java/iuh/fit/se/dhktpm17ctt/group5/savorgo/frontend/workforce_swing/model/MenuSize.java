package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuSize {
	@JsonProperty("size_name")
    private String sizeName;
	@JsonProperty("price_change")
    private double priceChange;
}
