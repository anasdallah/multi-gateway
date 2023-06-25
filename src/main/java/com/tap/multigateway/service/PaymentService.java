package com.tap.multigateway.service;

import com.tap.multigateway.constant.ApiErrors;
import com.tap.multigateway.dto.GatewayTransactionStatus;
import com.tap.multigateway.dto.PaymentResponse;
import com.tap.multigateway.entity.Transaction;
import com.tap.multigateway.exception.TapException;
import com.tap.multigateway.lock.DistributedLock;
import com.tap.multigateway.dto.PaymentRequest;
import com.tap.multigateway.factory.PaymentGatewayFactory;
import com.tap.multigateway.gateway.PaymentGateway;
import com.tap.multigateway.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentGatewayFactory paymentGatewayFactory;

    private final TransactionRepository transactionRepository;

    @DistributedLock(lockKey = "uuid")
    public PaymentResponse initiatePayment(PaymentRequest paymentRequest) throws URISyntaxException {

        transactionRepository.findById(paymentRequest.getUuid())
                .ifPresent(trx -> {
                    throw TapException.badRequest(ApiErrors.REQUEST_ALREADY_PROCESSED_BEFORE, trx
                            .getUuid());
                });

        log.info("The Preferred Payment Gateway for request with id {}: {}", paymentRequest.getUuid(), paymentRequest.getPreferredGateway());

        PaymentGateway paymentGateway = paymentGatewayFactory.getPaymentGateway(paymentRequest.getPreferredGateway());

        String paymentResponseStatus = paymentGateway.processPayment(paymentRequest);

        Pair<String, String> paymentResult = Pair.of(paymentResponseStatus, paymentRequest.getPreferredGateway());

        if (paymentResponseStatus.equals(GatewayTransactionStatus.NOT_APPROVED.getStatus())) {
            paymentResult = processAlternativeGateways(paymentRequest);
        }

        handleResponseAndUpdateTransaction(paymentRequest, paymentResult);
        return new PaymentResponse(paymentRequest.getUuid(), paymentResult.getFirst());
    }

    private Pair<String, String> processAlternativeGateways(PaymentRequest paymentRequest) throws URISyntaxException {
        // Get list of all available gateways
        List<String> allGateways = new ArrayList<>(paymentGatewayFactory.getAllGateways());

        // Remove preferred gateway from the list as it has already failed
        allGateways.remove(paymentRequest.getPreferredGateway());

        String paymentResponse = GatewayTransactionStatus.NOT_APPROVED.getStatus();
        String processedGateway = "";

        // Business logic for selecting alternative gateway
        for (String gateway : allGateways) {
            PaymentGateway alternativePaymentGateway = paymentGatewayFactory.getPaymentGateway(gateway);

            paymentResponse = alternativePaymentGateway.processPayment(paymentRequest);

            // If payment is successful with the alternative gateway, break the loop
            if (paymentResponse.equals(GatewayTransactionStatus.APPROVED.getStatus())) {
                processedGateway = gateway;
                break;
            }
        }

        return Pair.of(paymentResponse, processedGateway);
    }


    private void handleResponseAndUpdateTransaction(PaymentRequest paymentRequest, Pair<String, String> paymentResult) {
        String paymentResponseStatus = paymentResult.getFirst();
        String processedGateway = paymentResult.getSecond();

        if (paymentResponseStatus.equals(GatewayTransactionStatus.PENDING.getStatus())) {
            log.info("Timeout occurred for request with id: {}", paymentRequest.getUuid());
            transactionRepository.save(convertRequestToTransactionEntity(paymentRequest, GatewayTransactionStatus.PENDING.getStatus(), processedGateway));
        } else if (paymentResponseStatus.equals(GatewayTransactionStatus.APPROVED.getStatus())) {
            log.info("Payment approved for request with id: {}", paymentRequest.getUuid());
            transactionRepository.save(convertRequestToTransactionEntity(paymentRequest, GatewayTransactionStatus.APPROVED.getStatus(), processedGateway));
        } else {
            log.info("Payment not approved in all gateways for request with id: {}", paymentRequest.getUuid());
            transactionRepository.save(convertRequestToTransactionEntity(paymentRequest, GatewayTransactionStatus.NOT_APPROVED.getStatus(), processedGateway));
        }
    }

    private Transaction convertRequestToTransactionEntity(PaymentRequest paymentRequest, String status,
                                                          String processedGateway) {

        return Transaction
                .builder()
                .uuid(paymentRequest.getUuid())
                .amount(paymentRequest.getAmount())
                .currency(paymentRequest.getCurrency())
                .payeeId(paymentRequest.getPayeeId())
                .payerId(paymentRequest.getPayerId())
                .preferredGateway(paymentRequest.getPreferredGateway())
                .status(status)
                .processedGateway(processedGateway)
                .build();
    }

    private PaymentRequest convertTransactionEntityToRequest(Transaction transaction) {

        return PaymentRequest
                .builder()
                .uuid(transaction.getUuid())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .payeeId(transaction.getPayeeId())
                .payerId(transaction.getPayerId())
                .preferredGateway(transaction.getPreferredGateway())
                .build();
    }


    public PaymentResponse paymentStatus(final String transactionId) throws URISyntaxException {

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> TapException.badRequest(ApiErrors.NOT_FOUND, "transaction with id: " + transactionId));

        if (!transaction.getStatus().equals(GatewayTransactionStatus.PENDING.getStatus())) {
            return new PaymentResponse(transactionId, transaction.getStatus());
        }

        String paymentGateway = transaction.getProcessedGateway();
        String paymentResponseStatus = paymentGatewayFactory.getPaymentGateway(paymentGateway).paymentStatus(transactionId);

        handleResponseAndUpdateTransaction(convertTransactionEntityToRequest(transaction),
                Pair.of(paymentResponseStatus, paymentGateway));

        return new PaymentResponse(transactionId, paymentResponseStatus);
    }
}
