package com.orgname.qa.lib.utils.logger;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoggerSupplier {

    static final int STACK_TRACE_ID = 2;

    public static Logger getLogger() {
        return LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[STACK_TRACE_ID].getClassName());
    }
}
