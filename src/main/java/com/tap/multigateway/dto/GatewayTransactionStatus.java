package com.tap.multigateway.dto;

public enum GatewayTransactionStatus {
    APPROVED("Approved"),
    NOT_APPROVED("Not Approved"),
    PENDING("Pending");

    private final String status;

    GatewayTransactionStatus(final String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }


}
