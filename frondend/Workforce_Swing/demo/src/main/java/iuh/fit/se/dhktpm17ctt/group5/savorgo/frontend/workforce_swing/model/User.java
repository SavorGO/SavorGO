package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model;

import java.io.IOException;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.UserRoleEnum;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.UserTierEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User extends ModelBasic {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private UserRoleEnum role;
    private int points;
    private UserTierEnum tier;
    private String address;
    private String publicId;
}