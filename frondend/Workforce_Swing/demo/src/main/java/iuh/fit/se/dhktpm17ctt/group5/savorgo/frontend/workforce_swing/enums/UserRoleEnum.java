package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums;

import java.util.Arrays;

public enum UserRoleEnum {
    GUEST("Guest"),
    CUSTOMER("Customer"),
    STAFF("Staff"),
    MANAGER("Manager");

    private final String displayName;

    // Constructor
    UserRoleEnum(String displayName) {
        this.displayName = displayName;
    }

    // Getter for display name
    public String getDisplayName() {
        return displayName;
    }

    public static UserRoleEnum fromDisplayName(String displayName) {
        for (UserRoleEnum role : UserRoleEnum.values()) {
            if (role.getDisplayName().equalsIgnoreCase(displayName)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No enum constant with display name " + displayName);
    }

    public static String[] getDisplayNames() {
        return Arrays.stream(UserRoleEnum.values())
                .map(UserRoleEnum::getDisplayName)
                .toArray(String[]::new);
    }
}