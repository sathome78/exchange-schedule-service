package me.exrates.scheduleservice.models.enums;

import java.util.Arrays;

public enum UserRole {

    ADMINISTRATOR(1),
    ACCOUNTANT(2),
    ADMIN_USER(3),
    USER(4),
    ROLE_CHANGE_PASSWORD(5),
    EXCHANGE(6),
    VIP_USER(7),
    TRADER(8),
    FIN_OPERATOR(9),
    BOT_TRADER(10, false, false),
    ICO_MARKET_MAKER(11),
    OUTER_MARKET_BOT(12);

    private final int role;
    private final boolean showExtendedOrderInfo;
    private final boolean isReal;

    UserRole(int role, boolean showExtendedOrderInfo, boolean isReal) {
        this.role = role;
        this.showExtendedOrderInfo = showExtendedOrderInfo;
        this.isReal = isReal;
    }

    UserRole(int role) {
        this(role, true, true);
    }

    public int getRole() {
        return role;
    }

    public boolean showExtendedOrderInfo() {
        return showExtendedOrderInfo;
    }

    public static UserRole convert(int id) {
        return Arrays.stream(UserRole.class.getEnumConstants())
                .filter(e -> e.role == id)
                .findAny().orElse(USER);
    }
}