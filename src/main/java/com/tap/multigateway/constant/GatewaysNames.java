package com.tap.multigateway.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum GatewaysNames {

    PAYPAL("PayPal"),
    STRIPE("Stripe");

    private final String name;

    public String getName() {
        return name;
    }
}
