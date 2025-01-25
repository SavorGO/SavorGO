package raven.modal.demo.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EnumMenuStatus {
    AVAILABLE("Available"),       // Món ăn có sẵn, có thể gọi
    OUT_OF_STOCK("Out of Stock"), // Món ăn hết nguyên liệu, không thể gọi
    DISCONTINUED("Discontinued"), // Món ăn đã ngừng cung cấp
    DELETED("Deleted");           // Món ăn đã bị xóa khỏi hệ thống

    private final String displayName;

    // Constructor
    EnumMenuStatus(String displayName) {
        this.displayName = displayName;
    }
    @JsonValue
    // Getter cho tên hiển thị (nếu cần)
    public String getDisplayName() {
        return displayName;
    }
    @JsonCreator
    // Phương thức chuyển từ tên hiển thị sang enum
    public static EnumMenuStatus fromDisplayName(String displayName) {
        for (EnumMenuStatus status : EnumMenuStatus.values()) {
            if (status.getDisplayName().equalsIgnoreCase(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with display name " + displayName);
    }
    // return String[]
    public static String[] getDisplayNames() {
        return java.util.Arrays.stream(EnumMenuStatus.values())
                .filter(status -> status != EnumMenuStatus.DELETED) // Bỏ qua Deleted
                .map(EnumMenuStatus::getDisplayName)                // Lấy displayName
                .toArray(String[]::new);                            // Chuyển thành mảng
    }
}
