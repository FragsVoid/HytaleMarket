package dev.hytalemodding.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class NumberUtils {

    private final static NumberFormat numberFormat;

    static {
        numberFormat = NumberFormat.getInstance(Locale.forLanguageTag("es-ES"));
        numberFormat.setMaximumFractionDigits(2);
    }

    public static String formatNumber(double number) {
        return numberFormat.format(number);
    }

    public static String formatNumber(long number) {
        return numberFormat.format(number);
    }

    public static String formatNumber(int number) {
        return numberFormat.format(number);
    }
}
