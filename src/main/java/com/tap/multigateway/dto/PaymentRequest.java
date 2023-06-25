package com.tap.multigateway.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.tap.multigateway.validation.annotation.Currency;
import com.tap.multigateway.validation.annotation.Gateway;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PaymentRequest {


    @NotBlank
    private String uuid;

    @NotBlank
    private String payerId;

    @NotBlank
    private String payeeId;

    @NotNull
    private BigDecimal amount;

    @Currency
    private String currency;

    @Gateway
    private String preferredGateway;
}
