package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums;

import java.util.Arrays;

public enum PromotionStatusEnum {
    AVAILABLE("Available"), // Khuyến mãi đang hoạt động
    ENDED("Ended"),         // Khuyến mãi đã kết thúc
    DELETED("Deleted");     // Khuyến mãi đã bị xóa

    private final String displayName;

    // Constructor
    PromotionStatusEnum(String displayName) {
        this.displayName = displayName;
    }

    // Getter cho tên hiển thị
    public String getDisplayName() {
        return displayName;
    }

    public static PromotionStatusEnum fromDisplayName(String displayName) {
        for (PromotionStatusEnum status : PromotionStatusEnum.values()) {
            if (status.getDisplayName().equalsIgnoreCase(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with display name " + displayName);
    }

    public static String[] getDisplayNames() {
        return Arrays.stream(PromotionStatusEnum.values())
                .map(PromotionStatusEnum::getDisplayName)
                .toArray(String[]::new);
    }
}