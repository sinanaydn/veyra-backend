package com.veyra.rentacar.core.utilities;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public final class SlugHelper {

    private SlugHelper() {}

    public static String generate(String... parts) {
        return Arrays.stream(parts)
                .filter(p -> p != null && !p.isEmpty())
                .map(SlugHelper::normalize)
                .collect(Collectors.joining("-"));
    }

    private static String normalize(String input) {
        return input
                .replace("Ş", "s").replace("ş", "s")
                .replace("Ğ", "g").replace("ğ", "g")
                .replace("Ü", "u").replace("ü", "u")
                .replace("Ö", "o").replace("ö", "o")
                .replace("İ", "i").replace("ı", "i")
                .replace("Ç", "c").replace("ç", "c")
                .replace("I", "i")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }
}
