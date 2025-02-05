package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import com.cloudinary.provisioning.Account.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.MenuCategoryEnum;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.MenuStatusEnum;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.UserRoleEnum;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.UserTierEnum;
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
public class User extends ModelBasic {
	private int id;
	private String name;
	private String email;
    private String firstName;
    private String lastName;
    private UserRoleEnum role;
    private int points;
    private UserTierEnum tier;
    private String address;
    private UserRoleEnum userRoleEnum;
    private String publicId;
}
