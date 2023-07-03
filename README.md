# multi-gateway

## Overview

This repository contains the source code for a Multi-Gateway Payment Processing API. The API is designed to handle payments through different gateways including PayPal and Stripe, while managing to prevent double spending and handling other edge cases in payment processing.

The design leverages SOLID principles and the Factory Design Pattern to dynamically instantiate different payment gateway handlers. This makes the system flexible and extensible, allowing for new gateways to be added with minimal code changes.

## Dependencies
Before setting up this project, you need to clone and run two other services:

 • PayPal Gateway: You can clone it from this repository: [paypal repository](https://github.com/anasdallah/paypal).
 
 • Stripe Gateway: You can clone it from this repository: [Stripe repository](https://github.com/anasdallah/stripe). 

Both of these services should be up and running before starting the Multi-Gateway Payment Processing API.


## Setup
   1. Clone this repository.
    
   2. Navigate to the project directory.
  
   3. Make sure the PayPal and Stripe gateway services are running.
   
   4. Run ```docker-compose up``` in the terminal to spin up Redis and MySQL services.
   
   5. ```mvn clean install```
  
   6. Run the application.


## Usage

After the server is up and running, you can use an API client like Postman or cURL to hit the API endpoints.

For example, the endpoint for initiating a payment would be: 
```
curl --location --request POST 'localhost:8080/payments/v1/initiate' \
--header 'Content-Type: application/json' \
--data '{
    "uuid":"6ebf5840-1e38-48d4-a5b2-46a67d6cc623",
    "payer_id": "user123",
    "payee_id": "store456",
    "amount": 1000.00,
    "currency": "USD",
    "preferred_gateway": "Stripe"
}'
```

The API expects a PaymentRequest object in the request body with details like the UUID of the transaction, payer ID, payee ID, amount, currency, and the preferred payment gateway.

A successful response will return the transaction UUID and status of the payment, indicating whether the payment was approved, pending, or not approved.

**Note** If the request take more than 30 seconde the payment status will be pending, and to check the status for it you should use this endpoint:
```
curl --location --request GET 'localhost:8080/payments/v1/status/6ebf5840-1e38-48d4-a5b2-46a67d6cc623'
```


## Design Principles, Patterns and Strategies

   1. SOLID principles: The code follows SOLID principles, which makes it easier to maintain and extend.

   2. Factory Design Pattern: The Factory Design Pattern is used to create instances of different payment gateways dynamically. This flexibility allows for more gateways to be added in the future with ease.

   3. Distributed Locking Mechanism: To prevent double spending, we use Redis to implement a distributed lock based on the UUID of the transaction. This ensures that multiple simultaneous requests with the same UUID cannot be processed.

   4. Fallback Mechanism: In case a payment fails on the preferred gateway, the system attempts to process the payment through the other available gateways. This increases the chances of successful payment processing.

   5. Timeout Handling: If a payment request take more than 30 seconds the status will be pending (due to a timeout, and we don't know what happend in the gateway), so the transaction is saved with a pending status. then you should query it later to check the final status.

   6. Gateway Switching: The design allows for easy switching between gateways. If the preferred gateway fails, the system automatically switches to another available gateway and attempts to process the payment.
   
   7. Circuit Breaker Pattern: To enhance the robustness of the system, a Circuit Breaker pattern is utilized. This pattern detects failures and encapsulates the logic of preventing a failure from constantly recurring during maintenance, temporary external system failure, or unexpected system difficulties.
