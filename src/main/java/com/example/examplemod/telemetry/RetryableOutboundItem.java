package com.example.examplemod.telemetry;

public class RetryableOutboundItem<T> {
    private T _payload;
    private int _maxRetries;
    private int _retryCount;

    public RetryableOutboundItem(T payload, int maxRetries) {
        this._payload = payload;
        this._maxRetries = maxRetries;
        this._retryCount = 0;
    }

    public T getPayload() {
        return _payload;
    }

    public int getMaxRetries() {
        return _maxRetries;
    }

    public int getRetryCount() {
        return _retryCount;
    }

    public void incrementRetryCount() {
        _retryCount++;
    }
}