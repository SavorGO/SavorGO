package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums;

import java.util.Arrays;

public enum UserTierEnum {
    NONE("None"),
    COPPER("Copper"),
    GOLD("Gold"),
    DIAMOND("Diamond");

    private final String displayName;

    // Constructor
    UserTierEnum(String displayName) {
        this.displayName = displayName;
    }

    // Getter for display name
    public String getDisplayName() {
        return displayName;
    }

    public static UserTierEnum fromDisplayName(String displayName) {
        for (UserTierEnum tier : UserTierEnum.values()) {
            if (tier.getDisplayName().equalsIgnoreCase(displayName)) {
                return tier;
            }
        }
        throw new IllegalArgumentException("No enum constant with display name " + displayName);
    }

    public static String[] getDisplayNames() {
        return Arrays.stream(UserTierEnum.values())
                .map(UserTierEnum::getDisplayName)
                .toArray(String[]::new);
    }
}