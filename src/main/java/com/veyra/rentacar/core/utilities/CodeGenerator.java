package com.veyra.rentacar.core.utilities;

import java.security.SecureRandom;
import java.util.Random;

public final class CodeGenerator {

    private CodeGenerator() {}

    // "VYR-" + 6 karakter uppercase alphanumeric
    public static String generateReservationCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder("VYR-");
        Random random = new SecureRandom();
        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();  // örnek: "VYR-K2M9NP"
    }
}
