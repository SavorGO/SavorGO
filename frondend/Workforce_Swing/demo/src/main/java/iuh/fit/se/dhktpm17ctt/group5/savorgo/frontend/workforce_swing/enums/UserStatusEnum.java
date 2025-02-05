package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums;

import java.util.Arrays;

public enum UserStatusEnum {
    OK("OK"),
    DELETED("Deleted");

    private final String displayName;

    // Constructor
    UserStatusEnum(String displayName) {
        this.displayName = displayName;
    }

    // Getter for display name
    public String getDisplayName() {
        return displayName;
    }

    public static UserStatusEnum fromDisplayName(String displayName) {
        for (UserStatusEnum status : UserStatusEnum.values()) {
            if (status.getDisplayName().equalsIgnoreCase(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with display name " + displayName);
    }

    public static String[] getDisplayNames() {
        return Arrays.stream(UserStatusEnum.values())
                .map(UserStatusEnum::getDisplayName)
                .toArray(String[]::new);
    }
}