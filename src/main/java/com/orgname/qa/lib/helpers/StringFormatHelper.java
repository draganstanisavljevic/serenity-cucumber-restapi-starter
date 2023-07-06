package com.orgname.qa.lib.helpers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.text.CaseUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringFormatHelper {
    public static String toCamelCase(final String txt) {
        return CaseUtils.toCamelCase(txt, false);
    }
}
