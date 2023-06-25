package com.tap.multigateway.client.paypal;

import com.tap.multigateway.dto.PaymentRequest;
import com.tap.multigateway.exception.TapException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
public class PayPalFallBack implements PayPalGatewayClient {

    private TapException exception;

    public PayPalFallBack() {
    }

    public PayPalFallBack(final TapException exception) {
        this.exception = exception;
    }

    @Override
    public ResponseEntity<String> processPayment(PaymentRequest paymentRequest) throws TapException, URISyntaxException {
        if (exception != null) {
            throw exception;
        }
        log.info("Paypal FallBack!");
        return ResponseEntity.created(new URI("")).body("Not Approved");
    }

    @Override
    public ResponseEntity<String> paymentStatus(final String uuid) throws TapException, URISyntaxException {
        return ResponseEntity.created(new URI("")).body("Not Approved");
    }
}
