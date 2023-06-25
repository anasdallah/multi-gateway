package com.tap.multigateway.client.stripe;

import com.tap.multigateway.dto.PaymentRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
public class StripeTimeoutFallback implements StripeGatewayClient {


    @Override
    public ResponseEntity<String> processPayment(PaymentRequest paymentRequest) throws URISyntaxException {
        log.info("Stripe Timeout FallBack!");
        return ResponseEntity.created(new URI("")).body("Pending");
    }

    @Override
    public ResponseEntity<String> paymentStatus(final String uuid) throws URISyntaxException {
        return ResponseEntity.created(new URI("")).body("Pending");
    }
}