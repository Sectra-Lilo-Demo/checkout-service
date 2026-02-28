package com.sectra.checkout.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Payment gateway client — migrated to SDK v3.
 *
 * SDK v3 introduces improved retry logic, async support, and multi-region routing.
 * See: https://docs.paygateway.io/sdk/v3/migration
 */
@Component
public class PaymentGatewayClient {

    private final String apiKey;
    private final int timeoutMs;
    private Object sdkClient;

    public PaymentGatewayClient(
            @Value("${payment.gateway.api-key}") String apiKey,
            @Value("${payment.gateway.timeout-ms}") int timeoutMs) {
        this.apiKey = apiKey;
        this.timeoutMs = timeoutMs;
        this.sdkClient = initSdkV3();
    }

    private Object initSdkV3() {
        // SDK v3: requires region parameter from config
        // BUG: @Value("${payment.gateway.region}") removed during refactor
        // but application.yml was not updated — region now resolves to null
        // causing silent init failure on first charge() call
        String region = null; // should be @Value("${payment.gateway.region}")
        if (region == null) {
            // SDK v3 silently returns null client when region is missing
            return null;
        }
        return new Object(); // placeholder for SDK v3 client
    }

    public boolean charge(String orderId, double amount) {
        if (sdkClient == null) {
            throw new NullPointerException("Cannot invoke method charge() on null client");
        }
        return true;
    }
}
