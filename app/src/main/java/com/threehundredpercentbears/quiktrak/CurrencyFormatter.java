package com.threehundredpercentbears.quiktrak;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;

public class CurrencyFormatter {

    public static String createCurrencyFormattedString(int value) {
        return NumberFormat.getCurrencyInstance().format(intToBigDecimal(value));
    }

    private static BigDecimal intToBigDecimal(int storedValue) {
        return new BigDecimal(storedValue).setScale(2, RoundingMode.HALF_UP).divide(new BigDecimal(100), RoundingMode.HALF_UP);
    }
}
