package com.tap.multigateway.controller;


import com.tap.multigateway.api.BaseResponse;
import com.tap.multigateway.api.BaseResponseBuilder;
import com.tap.multigateway.dto.PaymentRequest;
import com.tap.multigateway.dto.PaymentResponse;
import com.tap.multigateway.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;


@RestController
@RequestMapping("/payments/v1")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate")
    public ResponseEntity<BaseResponse<PaymentResponse>> initiatePayment(@RequestBody @Validated PaymentRequest paymentRequest) throws URISyntaxException {
        return BaseResponseBuilder.created(paymentService.initiatePayment(paymentRequest));
    }


    @GetMapping("/status/{transaction_id}")
    public ResponseEntity<BaseResponse<PaymentResponse>> paymentStatus(@PathVariable("transaction_id") String transactionId) throws URISyntaxException {
        return BaseResponseBuilder.created(paymentService.paymentStatus(transactionId));
    }

}
