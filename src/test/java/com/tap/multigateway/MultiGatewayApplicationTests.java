package com.tap.multigateway;

import com.tap.multigateway.client.paypal.PayPalGatewayClient;
import com.tap.multigateway.client.stripe.StripeGatewayClient;
import com.tap.multigateway.controller.PaymentController;
import com.tap.multigateway.dto.GatewayTransactionStatus;
import com.tap.multigateway.entity.Transaction;
import com.tap.multigateway.factory.PaymentGatewayFactory;
import com.tap.multigateway.gateway.PaypalPaymentGateway;
import com.tap.multigateway.gateway.StripePaymentGateway;
import com.tap.multigateway.repository.TransactionRepository;
import com.tap.multigateway.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {PaymentController.class})
class MultiGatewayApplicationTests {

    @SpyBean
    private PaymentService paymentService;

    @MockBean
    private PaymentGatewayFactory paymentGatewayFactory;

    @MockBean
    PayPalGatewayClient payPalGatewayClient;

    @MockBean
    StripeGatewayClient stripeGatewayClient;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionRepository transactionRepository;

    @SpyBean
    private PaypalPaymentGateway paypalPaymentGateway;

    @SpyBean
    private StripePaymentGateway stripePaymentGateway;

    @Test
    void whenValidRequest_thenReturnsApproved() throws Exception {
        String paymentRequestJson = "{ \"uuid\": \"trans001\", \"payer_id\": \"user123\", \"payee_id\": \"store456\", \"amount\": 1000.00, \"currency\": \"JPY\", \"preferred_gateway\": \"PayPal\" }";
        String paymentStatus = GatewayTransactionStatus.APPROVED.getStatus();

        ResponseEntity<String> response = ResponseEntity.created(new URI("")).body(paymentStatus);
        when(payPalGatewayClient.processPayment(any())).thenReturn(response);
        when(paymentGatewayFactory.getPaymentGateway(anyString())).thenReturn(paypalPaymentGateway);
        mockMvc.perform(post("/payments/v1/initiate")
                        .content(paymentRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response_body.status", is(paymentStatus)));
    }

    @Test
    void whenInvalidPaymentRequest_thenReturnBadRequest() throws Exception {
        // Prepare test data
        String invalidPaymentRequestJson = "{}";

        // Send request and validate response
        mockMvc.perform(post("/payments/v1/initiate")
                        .content(invalidPaymentRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenPaymentGatewayNotFound_thenReturnBadRequest() throws Exception {
        // Prepare test data
        String paymentRequestJson = "{ \"uuid\": \"trans001\", \"payer_id\": \"user123\", \"payee_id\": \"store456\", \"amount\": 1000.00, \"currency\": \"JPY\", \"preferred_gateway\": \"Unknown\" }";

        // Mock dependencies
        when(paymentGatewayFactory.getPaymentGateway(anyString())).thenReturn(null);

        // Send request and validate response
        mockMvc.perform(post("/payments/v1/initiate")
                        .content(paymentRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenPaymentRequestAlreadyProcessed_thenReturnBadRequest() throws Exception {
        // Prepare test data
        String paymentRequestJson = "{ \"uuid\": \"trans001\", \"payer_id\": \"user123\", \"payee_id\": \"store456\", \"amount\": 1000.00, \"currency\": \"JPY\", \"preferred_gateway\": \"Unknown\" }";
        String transactionId = "trans001";

        // Mock dependencies
        when(transactionRepository.findById(transactionId)).thenReturn(java.util.Optional.of(new Transaction()));

        // Send request and validate response
        mockMvc.perform(post("/payments/v1/initiate")
                        .content(paymentRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenAlternativeGatewayProcessed_thenReturnSuccess() throws Exception {
        // Prepare test data
        String paymentRequestJson = "{ \"uuid\": \"trans001\", \"payer_id\": \"user123\", \"payee_id\": \"store456\", \"amount\": 1000.00, \"currency\": \"JPY\", \"preferred_gateway\": \"PayPal\" }";

        ResponseEntity<String> approveResponse = ResponseEntity.created(new URI("")).body(GatewayTransactionStatus.APPROVED.getStatus());
        ResponseEntity<String> notApproveResponse = ResponseEntity.created(new URI("")).body(GatewayTransactionStatus.NOT_APPROVED.getStatus());


        // Mock dependencies
        when(paymentGatewayFactory.getPaymentGateway(anyString())).thenReturn(paypalPaymentGateway);
        when(payPalGatewayClient.processPayment(any())).thenReturn(notApproveResponse);
        when(paymentGatewayFactory.getPaymentGateway(anyString())).thenReturn(stripePaymentGateway);
        when(stripeGatewayClient.processPayment(any())).thenReturn(approveResponse);


        // Send request and validate response
        mockMvc.perform(post("/payments/v1/initiate")
                        .content(paymentRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response_body.status", is(GatewayTransactionStatus.APPROVED.getStatus())));
    }

    @Test
    void whenRetrievePaymentStatus_thenReturnSuccess() throws Exception {
        // Prepare test data
        String transactionId = "trans001";
        String paymentStatus = GatewayTransactionStatus.APPROVED.getStatus();

        // Mock dependencies
        when(paymentGatewayFactory.getPaymentGateway(anyString())).thenReturn(paypalPaymentGateway);
        when(transactionRepository.findById(transactionId)).thenReturn(java.util.Optional.of(Transaction.builder().uuid(transactionId)
                .processedGateway("PayPal").status(GatewayTransactionStatus.PENDING.getStatus()).build()));
        when(payPalGatewayClient.paymentStatus(anyString())).thenReturn(ResponseEntity.ok().body(paymentStatus));

        // Send request and validate response
        mockMvc.perform(get("/payments/v1/status/{transactionId}", transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response_body.status").value(paymentStatus));
    }
}
