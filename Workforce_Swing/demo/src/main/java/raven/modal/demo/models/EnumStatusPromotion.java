package raven.modal.demo.models;

import java.util.Arrays;

public enum EnumStatusPromotion {
    AVAILABLE("Available"), // Khuyến mãi đang hoạt động
    ENDED("Ended"),         // Khuyến mãi đã kết thúc
    DELETED("Deleted");     // Khuyến mãi đã bị xóa

    private final String displayName;

    // Constructor
    EnumStatusPromotion(String displayName) {
        this.displayName = displayName;
    }

    // Getter cho tên hiển thị
    public String getDisplayName() {
        return displayName;
    }

    public static EnumStatusPromotion fromDisplayName(String displayName) {
        for (EnumStatusPromotion status : EnumStatusPromotion.values()) {
            if (status.getDisplayName().equalsIgnoreCase(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with display name " + displayName);
    }

    public static String[] getDisplayNames() {
        return Arrays.stream(EnumStatusPromotion.values())
                .map(EnumStatusPromotion::getDisplayName)
                .toArray(String[]::new);
    }
}