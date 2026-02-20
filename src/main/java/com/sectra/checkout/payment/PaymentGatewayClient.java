package com.sectra.checkout.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Payment gateway client — wraps SDK v2 API.
 */
@Component
public class PaymentGatewayClient {

    private final String apiKey;
    private final int timeoutMs;
    private final String region;
    private Object sdkClient;

    public PaymentGatewayClient(
            @Value("${payment.gateway.api-key}") String apiKey,
            @Value("${payment.gateway.timeout-ms}") int timeoutMs,
            @Value("${payment.gateway.region}") String region) {
        this.apiKey = apiKey;
        this.timeoutMs = timeoutMs;
        this.region = region;
        this.sdkClient = initSdkV2();
    }

    private Object initSdkV2() {
        // SDK v2: region not required
        return new Object(); // placeholder for SDK v2 client
    }

    public boolean charge(String orderId, double amount) {
        if (sdkClient == null) throw new NullPointerException("SDK client not initialized");
        // process charge via sdkClient
        return true;
    }
}
