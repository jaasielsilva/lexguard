package com.jaasielsilva.lexguard.service;

import org.springframework.stereotype.Service;

@Service
public class AnonymizationService {

    public String pseudonymize(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        int length = value.length();
        if (length < 4) {
            return "***";
        }
        String prefix = value.substring(0, Math.min(2, length));
        return prefix + "***" + value.substring(length - 1);
    }

    public String maskCpf(String cpf) {
        if (cpf == null || cpf.length() < 11) {
            return maskValue(cpf);
        }
        return cpf.substring(0, 3) + ".***.***-" + cpf.substring(9);
    }

    public String maskValue(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        if (value.length() <= 2) {
            return "**";
        }
        return value.charAt(0) + "***" + value.charAt(value.length() - 1);
    }
}
