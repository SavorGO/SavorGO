package iuh.fit.se.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(1001, "Uncategorized exception"),
    USER_EXITS(1002, "User already exists"),
    NAME_INVALID(1003, "Name must not contain numbers or special characters"),
    EMAIL_INVALID(1004, "Email must be in the correct format"),
    USER_NOT_FOUND(1005, "User not found"),
    PASSWORD_INCORRECT(1006, "Password is incorrect"),
    PASSWORD_INVALID(
            1007,
            "Password must contain at least one uppercase letter,"
                    + " one special character, and be at least 8 characters long"
                    + "Ex: Thinh@123"),
    UNAUTHENTICATED(1008, "Email or password is incorrect"),
    ;
    int code;
    String message;
}
