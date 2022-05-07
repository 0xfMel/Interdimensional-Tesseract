package ftm._0xfmel.itdmtrct.utils;

import ftm._0xfmel.itdmtrct.utils.exception.ValidationException;

public class ValidationUtil {
    public static void assertLength(String value, int minLen, int maxLen, String desc) throws ValidationException {
        if (value.length() <= maxLen && value.length() >= minLen)
            return;

        throw new ValidationException(
                "\"" + desc + "\" failed length assertion of min: " + minLen + ", max: " + maxLen);
    }
}
