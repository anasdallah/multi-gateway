package com.tap.multigateway.gateway;

import com.tap.multigateway.client.paypal.PayPalGatewayClient;
import com.tap.multigateway.dto.PaymentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;


@Component("PayPal")
@RequiredArgsConstructor
@Slf4j
public class PaypalPaymentGateway implements PaymentGateway {

    private final PayPalGatewayClient payPalGatewayClient;

    @Override
    public String processPayment(PaymentRequest paymentRequest) throws URISyntaxException {
        return payPalGatewayClient.processPayment(paymentRequest).getBody();
    }

    @Override
    public String paymentStatus(final String transactionId) throws URISyntaxException {
        return payPalGatewayClient.paymentStatus(transactionId).getBody();
    }
}