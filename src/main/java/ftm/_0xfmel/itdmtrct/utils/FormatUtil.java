package ftm._0xfmel.itdmtrct.utils;

import java.text.DecimalFormat;

public class FormatUtil {
    public static String formatNumber(int number, String suffix, boolean noRedundentDecimal) {
        return (noRedundentDecimal ? FormatUtil.withSuffixNoRedundentDecimal(number) : FormatUtil.withSuffix(number))
                + suffix;
    }

    public static String formatNumber(int number, String suffix) {
        return FormatUtil.formatNumber(number, suffix, true);
    }

    public static String withSuffix(long count) {
        if (count < 1000)
            return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format("%.1f %c",
                count / Math.pow(1000, exp),
                "kMGTPE".charAt(exp - 1));
    }

    public static String withSuffixNoRedundentDecimal(long count) {
        if (count < 1000)
            return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        DecimalFormat format = new DecimalFormat("0.#");
        String value = format.format(count / Math.pow(1000, exp));
        return String.format("%s%c", value, "kMBTPE".charAt(exp - 1));
    }
}
