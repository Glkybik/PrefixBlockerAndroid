package com.example.prefixblocker.data;

public class Prefix {
    private String prefix;
    private boolean active;

    public Prefix(String prefix, boolean active) {
        this.prefix = prefix;
        this.active = active;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}