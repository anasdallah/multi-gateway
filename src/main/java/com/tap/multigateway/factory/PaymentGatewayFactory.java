package com.tap.multigateway.factory;

import com.tap.multigateway.gateway.PaymentGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class PaymentGatewayFactory {

    private final Map<String, PaymentGateway> paymentGateways;

    public PaymentGateway getPaymentGateway(String name) {

        PaymentGateway paymentGateway = paymentGateways.get(name);

        if (paymentGateway == null) {
            throw new IllegalArgumentException("No such payment gateway: " + name);
        }

        return paymentGateway;
    }

    public List<String> getAllGateways() {
        return new ArrayList<>(paymentGateways.keySet());
    }

}
