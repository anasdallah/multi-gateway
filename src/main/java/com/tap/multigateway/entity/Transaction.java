package com.tap.multigateway.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity(name = "TRANSACTIONS")
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    private String uuid;

    @Column(name = "PAYER_ID")
    private String payerId;

    @Column(name = "PAYEE_ID")
    private String payeeId;

    @Column(name = "AMOUNT", columnDefinition="DECIMAL(24, 12)")
    private BigDecimal amount;

    @Column(name = "CURRENCY")
    private String currency;

    @Column(name = "PREFERRED_GATEWAY")
    private String preferredGateway;

    @Column(name = "PROCESSED_GATEWAY")
    private String processedGateway;

    @Column(name = "STATUS")
    private String status;

}
