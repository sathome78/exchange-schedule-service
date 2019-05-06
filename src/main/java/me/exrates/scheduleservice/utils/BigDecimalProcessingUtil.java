package me.exrates.scheduleservice.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Contained constans and methods to operate with BigDecimal value
 */
@NoArgsConstructor(access = AccessLevel.NONE)
public final class BigDecimalProcessingUtil {

    private static final int SCALE = 9;
    private static final RoundingMode ROUND_TYPE = RoundingMode.HALF_UP;
    private static final String PATTERN = "###,##0." + new String(new char[SCALE]).replace("\0", "0");
    private static final String PATTERN_SHORT = "###,##0." + new String(new char[SCALE]).replace("\0", "#");

    /**
     * Returns String converted from BigDecimal value
     * with <b>No</b> group separator and <b>Comma</b> as decimal separator
     * with trailing zeros if trailingZeros is "true" or without if "false"
     *
     * @param bigDecimal value to convert
     * @return string ov value or "0" if value is null
     * 67553.116000000 => 67553,116 or 67553,116000000 (depending on trailingZeros)
     */
    public static String formatNoneComma(BigDecimal bigDecimal, boolean trailingZeros) {
        DecimalFormat df = new DecimalFormat(trailingZeros ? PATTERN : PATTERN_SHORT);
        DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
        df.setRoundingMode(ROUND_TYPE);
        df.setGroupingUsed(false);
        dfs.setDecimalSeparator(',');
        df.setDecimalFormatSymbols(dfs);
        return df.format(bigDecimal == null ? BigDecimal.ZERO : bigDecimal);
    }
}
