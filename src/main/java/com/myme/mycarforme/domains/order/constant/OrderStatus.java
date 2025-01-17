package com.myme.mycarforme.domains.order.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {
    CONTRACTED("CONTRACTED"),
    PAID("PAID"),
    DELIVERING("DELIVERING"),
    DELIVERED("DELIVERED"),
    UNKNOWN("UNKNOWN");

    private final String status;
}
