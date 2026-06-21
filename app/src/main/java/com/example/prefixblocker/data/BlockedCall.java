package com.example.prefixblocker.data;

public class BlockedCall {
    private String number;
    private String prefix;
    private String timestamp;

    public BlockedCall(String number, String prefix, String timestamp) {
        this.number = number;
        this.prefix = prefix;
        this.timestamp = timestamp;
    }

    public String getNumber() {
        return number;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getTimestamp() {
        return timestamp;
    }
}