package com.tap.multigateway.gateway;

import com.tap.multigateway.dto.PaymentRequest;

import java.net.URISyntaxException;

public interface PaymentGateway {

    String processPayment(PaymentRequest paymentRequest) throws URISyntaxException;

    String paymentStatus(String transactionId) throws URISyntaxException;

}
