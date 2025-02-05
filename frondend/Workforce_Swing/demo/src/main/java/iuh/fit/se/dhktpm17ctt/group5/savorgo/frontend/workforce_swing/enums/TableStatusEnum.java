package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums;

import java.util.Arrays;

public enum TableStatusEnum {
    AVAILABLE("Available"),       // Bàn trống, sẵn sàng sử dụng
    OUT_OF_SERVICE("Out of Service"), // Bàn không sử dụng được
    OCCUPIED("Occupied"),         // Bàn đang có người sử dụng
    NEEDS_CLEANING("Needs Cleaning"), // Bàn cần được dọn dẹp
    DELETED("Deleted");           // Bàn đã bị xóa khỏi hệ thống

    private final String displayName;

    // Constructor
    TableStatusEnum(String displayName) {
        this.displayName = displayName;
    }

    // Getter cho tên hiển thị (nếu cần)
    public String getDisplayName() {
        return displayName;
    }
    //
    public static TableStatusEnum fromDisplayName(String displayName) {
        for (TableStatusEnum status : TableStatusEnum.values()) {
            if (status.getDisplayName().equalsIgnoreCase(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with display name " + displayName);
    }
	public static String[] getDisplayNames() {
		String[] statuses = Arrays.stream(TableStatusEnum.values())
                .filter(status -> status != TableStatusEnum.DELETED) // Exclude DELETED status
                .map(TableStatusEnum::getDisplayName) // Get display names
                .toArray(String[]::new);
		return statuses;
	}

}
