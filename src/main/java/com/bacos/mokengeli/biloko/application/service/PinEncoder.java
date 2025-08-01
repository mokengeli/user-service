package com.bacos.mokengeli.biloko.application.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PinEncoder {
    private final BCryptPasswordEncoder delegate = new BCryptPasswordEncoder();

    public String encode(String raw) {
        return delegate.encode(raw);
    }

    public boolean matches(String raw, String hashed) {
        return delegate.matches(raw, hashed);
    }

    public boolean isSequential(String pin) {
        String asc = "0123456789";
        String desc = new StringBuilder(asc).reverse().toString();
        return asc.contains(pin) || desc.contains(pin);
    }

    public boolean isRepetitive(String pin) {
        return pin.chars().distinct().count() == 1;
    }
}
