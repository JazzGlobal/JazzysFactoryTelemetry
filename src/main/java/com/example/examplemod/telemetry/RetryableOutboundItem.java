package com.example.examplemod.telemetry;

public class RetryableOutboundItem<T> {
    public T Payload;
    public int MaxRetries;
    public int RetryCount;

    public RetryableOutboundItem(T payload, int maxRetries) {
        this.Payload = payload;
        this.MaxRetries = maxRetries;
        this.RetryCount = 0;
    }
}