package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums;

import java.util.Arrays;

public enum EnumDiscountType {
    PERCENT("Percent"), // Giảm giá theo phần trăm
    FLAT("Flat");       // Giảm giá theo số tiền cố định

    private final String displayName;

    // Constructor
    EnumDiscountType(String displayName) {
        this.displayName = displayName;
    }

    // Getter cho tên hiển thị
    public String getDisplayName() {
        return displayName;
    }

    public static EnumDiscountType fromDisplayName(String displayName) {
        for (EnumDiscountType type : EnumDiscountType.values()) {
            if (type.getDisplayName().equalsIgnoreCase(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with display name " + displayName);
    }

    public static String[] getDisplayNames() {
        return Arrays.stream(EnumDiscountType.values())
                .map(EnumDiscountType::getDisplayName)
                .toArray(String[]::new);
    }
}