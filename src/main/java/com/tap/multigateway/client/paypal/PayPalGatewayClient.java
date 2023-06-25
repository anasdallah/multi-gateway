package com.tap.multigateway.client.paypal;

import com.tap.multigateway.client.GatewayClientConfiguration;
import com.tap.multigateway.dto.PaymentRequest;
import com.tap.multigateway.exception.TapException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URISyntaxException;

@FeignClient(name = "paypal-gateway", url = "${feign.url.paypal}", configuration = PayPalClientConfiguration.class)
public interface PayPalGatewayClient {

    @PostMapping("/payment")
    ResponseEntity<String> processPayment(@RequestBody PaymentRequest paymentRequest) throws TapException, URISyntaxException;

    @GetMapping("/payments/{uuid}")
    ResponseEntity<String> paymentStatus(@PathVariable String uuid) throws TapException, URISyntaxException;

}
