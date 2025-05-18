package com.dtb.cardservice.Util;

import org.springframework.stereotype.Service;

@Service
public class MaskUtil {

    /**
     * Masks PAN to only show the first 6 and last 4 digits.
     * Example: 123456******7890
     */
    public String maskPan(String pan) {
        if (pan == null || pan.length() < 10) return "****";
        return pan.substring(0, 6) + "******" + pan.substring(pan.length() - 4);
    }

    /**
     * Masks CVV entirely. Example: ***
     */
    public String maskCvv(String cvv) {
        if (cvv == null) return "***";
        return "*".repeat(cvv.length());
    }
}
