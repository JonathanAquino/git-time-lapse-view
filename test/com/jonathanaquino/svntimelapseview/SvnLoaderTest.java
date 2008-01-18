package com.jonathanaquino.svntimelapseview;

import junit.framework.TestCase;

public class SvnLoaderTest extends TestCase {

    private class TestSvnLoader extends SvnLoader {
        public String formatDate(String date) {
            return super.formatDate(date);
        }
    }

    public void testFormatDate() {
        assertEquals("2007-09-16 10:18", new TestSvnLoader().formatDate("2007-09-16T10:18:10.143692Z"));
        assertEquals("2007-09-FOO", new TestSvnLoader().formatDate("2007-09-FOO"));
    }

}
