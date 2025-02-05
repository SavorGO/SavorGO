package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums;

import java.util.Arrays;

public enum StatusPromotionEnum {
    AVAILABLE("Available"), // Khuyến mãi đang hoạt động
    ENDED("Ended"),         // Khuyến mãi đã kết thúc
    DELETED("Deleted");     // Khuyến mãi đã bị xóa

    private final String displayName;

    // Constructor
    StatusPromotionEnum(String displayName) {
        this.displayName = displayName;
    }

    // Getter cho tên hiển thị
    public String getDisplayName() {
        return displayName;
    }

    public static StatusPromotionEnum fromDisplayName(String displayName) {
        for (StatusPromotionEnum status : StatusPromotionEnum.values()) {
            if (status.getDisplayName().equalsIgnoreCase(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with display name " + displayName);
    }

    public static String[] getDisplayNames() {
        return Arrays.stream(StatusPromotionEnum.values())
                .map(StatusPromotionEnum::getDisplayName)
                .toArray(String[]::new);
    }
}