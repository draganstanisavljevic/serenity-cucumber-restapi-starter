package com.orgname.qa.lib.webdriver;

import com.orgname.qa.lib.helpers.StringFormatHelper;

public class MyBaseObject extends BasePage {

    public void openPageUrl(final String baseUrl, final String pageName) {
        String path = String.format("pages.%s", StringFormatHelper.toCamelCase(pageName));
        super.openAt(baseUrl + env.getProperty(path));
    }
}
