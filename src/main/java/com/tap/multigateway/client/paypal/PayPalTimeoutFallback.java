package com.tap.multigateway.client.paypal;

import com.tap.multigateway.dto.PaymentRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
public class PayPalTimeoutFallback implements PayPalGatewayClient {


    @Override
    public ResponseEntity<String> processPayment(PaymentRequest paymentRequest) throws URISyntaxException {
        log.info("Paypal Timeout FallBack!");
        return ResponseEntity.created(new URI("")).body("Pending");
    }

    @Override
    public ResponseEntity<String> paymentStatus(final String uuid) throws URISyntaxException {
        return ResponseEntity.created(new URI("")).body("Pending");
    }
}