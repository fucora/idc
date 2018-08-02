package com.dmall.dispatcher.sdk.util;

import org.junit.Assert;
import org.junit.Test;

import com.iwellmass.dispatcher.sdk.util.DuplicateExecuteChecker;

public class DuplicateExecuteCheckerTest {

    @Test
    public void test() {
        Assert.assertTrue(DuplicateExecuteChecker.checkExecuteId(1, null, 0));
        for (long i = 0; i < 1000; i++) {
            Assert.assertFalse(DuplicateExecuteChecker.checkExecuteId(1, i, 0));
        }

        Assert.assertTrue(DuplicateExecuteChecker.checkExecuteId(1, null, 0));

        for (long i = 0; i < 1000; i++) {
            Assert.assertTrue(DuplicateExecuteChecker.checkExecuteId(1, i, 0));
        }

        for (long i = 2000; i < 4000; i++) {
            Assert.assertFalse(DuplicateExecuteChecker.checkExecuteId(1, i, 0));

            if (i == 2001) {
                for (long n = 2; n < 1000; n++) {
                    Assert.assertTrue(DuplicateExecuteChecker.checkExecuteId(1, n, 0));
                }
            }

            if (i == 3001) {
                for (long n = 2; n < 1000; n++) {
                    Assert.assertFalse(DuplicateExecuteChecker.checkExecuteId(1, n, 0));
                }
            }
        }
    }

}
