package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MenuStatusEnum {
    AVAILABLE("Available"),       // Món ăn có sẵn, có thể gọi
    OUT_OF_STOCK("Out of Stock"), // Món ăn hết nguyên liệu, không thể gọi
    DISCONTINUED("Discontinued"), // Món ăn đã ngừng cung cấp
    DELETED("Deleted");           // Món ăn đã bị xóa khỏi hệ thống

    private final String displayName;

    // Constructor
    MenuStatusEnum(String displayName) {
        this.displayName = displayName;
    }
    @JsonValue
    // Getter cho tên hiển thị (nếu cần)
    public String getDisplayName() {
        return displayName;
    }
    @JsonCreator
    // Phương thức chuyển từ tên hiển thị sang enum
    public static MenuStatusEnum fromDisplayName(String displayName) {
        for (MenuStatusEnum status : MenuStatusEnum.values()) {
            if (status.getDisplayName().equalsIgnoreCase(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with display name " + displayName);
    }
    // return String[]
    public static String[] getDisplayNames() {
        return java.util.Arrays.stream(MenuStatusEnum.values())
                .filter(status -> status != MenuStatusEnum.DELETED) // Bỏ qua Deleted
                .map(MenuStatusEnum::getDisplayName)                // Lấy displayName
                .toArray(String[]::new);                            // Chuyển thành mảng
    }
}
