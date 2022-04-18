package ftm._0xfmel.itdmtrct.utils;

import javax.xml.bind.ValidationException;

public class ValidationUtil {
    public static void assertLength(String value, int maxLen, String desc) throws ValidationException {
        if (value.length() <= maxLen)
            return;

        throw new ValidationException("\"" + desc + "\" failed max length assertion of " + maxLen);
    }
}
