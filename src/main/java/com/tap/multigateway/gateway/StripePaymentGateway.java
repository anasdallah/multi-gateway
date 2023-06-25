package com.tap.multigateway.gateway;

import com.tap.multigateway.client.stripe.StripeGatewayClient;
import com.tap.multigateway.dto.PaymentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import java.util.UUID;

@Component("Stripe")
@Slf4j
@RequiredArgsConstructor
public class StripePaymentGateway implements PaymentGateway {


    private final StripeGatewayClient stripeGatewayClient;

    @Override
    public String processPayment(PaymentRequest paymentRequest) throws URISyntaxException {
        return stripeGatewayClient.processPayment(paymentRequest).getBody();
    }

    @Override
    public String paymentStatus(final String transactionId) throws URISyntaxException {
        return stripeGatewayClient.paymentStatus(transactionId).getBody();
    }

}