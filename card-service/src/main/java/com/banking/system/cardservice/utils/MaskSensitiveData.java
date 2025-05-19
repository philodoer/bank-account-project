package com.banking.system.cardservice.utils;


/**
 * A util class to hide PAN and CVV data
 */
public class MaskSensitiveData {

    public static String maskPan(String pan) {
        if (pan == null || pan.length() < 4) return "****";
        int visibleDigits = 4;
        String maskedSection = "*".repeat(pan.length() - visibleDigits);
        return maskedSection + pan.substring(pan.length() - visibleDigits);
    }

    public static String maskCvv(String cvv) {
        if (cvv == null || cvv.isEmpty()) return "***";
        return "*".repeat(cvv.length());
    }
}
