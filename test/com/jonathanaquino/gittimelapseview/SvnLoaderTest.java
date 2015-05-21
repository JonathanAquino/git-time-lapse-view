package com.jonathanaquino.gittimelapseview;

import com.jonathanaquino.gittimelapseview.SvnLoader;

import junit.framework.TestCase;

public class SvnLoaderTest extends TestCase {

    private class TestSvnLoader extends SvnLoader {
        public String formatDate(int time, String timeZone) {
            return super.formatDate(time, timeZone);
        }
        protected String determineEncoding(byte[] array) {
            return super.determineEncoding(array);
        }
    }

    public void testFormatDate() {
        assertEquals("2007-09-16 10:18:10 GMT", new TestSvnLoader().formatDate(1189937890, "GMT"));
    }
    
    public void testDetermineEncoding() {
        assertNull(new TestSvnLoader().determineEncoding(new byte[] {}));
        assertNull(new TestSvnLoader().determineEncoding(new byte[] {(byte)0xEF, (byte)0xBB}));
        assertEquals("UTF-8", new TestSvnLoader().determineEncoding(new byte[] {(byte)0xEF, (byte)0xBB, 0}));
        assertEquals("UTF-16", new TestSvnLoader().determineEncoding(new byte[] {(byte)0xFE, (byte)0xFF, 0}));
        assertEquals("UTF-16", new TestSvnLoader().determineEncoding(new byte[] {(byte)0xFF, (byte)0xFE, 0}));
        assertNull(new TestSvnLoader().determineEncoding(new byte[] {(byte)0xFF, (byte)0xEE, 0}));
        
    }

}
