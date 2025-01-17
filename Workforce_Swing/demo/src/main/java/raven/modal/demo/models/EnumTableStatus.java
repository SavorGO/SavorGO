package raven.modal.demo.models;

public enum EnumTableStatus {
    AVAILABLE("Available"),       // Bàn trống, sẵn sàng sử dụng
    OUT_OF_SERVICE("Out of Service"), // Bàn không sử dụng được
    OCCUPIED("Occupied"),         // Bàn đang có người sử dụng
    NEEDS_CLEANING("Needs Cleaning"), // Bàn cần được dọn dẹp
    DELETED("Deleted");           // Bàn đã bị xóa khỏi hệ thống

    private final String displayName;

    // Constructor
    EnumTableStatus(String displayName) {
        this.displayName = displayName;
    }

    // Getter cho tên hiển thị (nếu cần)
    public String getDisplayName() {
        return displayName;
    }
    public static EnumTableStatus fromDisplayName(String displayName) {
        for (EnumTableStatus status : EnumTableStatus.values()) {
            if (status.getDisplayName().equalsIgnoreCase(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with display name " + displayName);
    }

}
