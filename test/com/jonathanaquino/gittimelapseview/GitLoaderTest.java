package com.jonathanaquino.gittimelapseview;

import com.jonathanaquino.gittimelapseview.GitLoader;

import junit.framework.TestCase;

public class GitLoaderTest extends TestCase {

    private class TestGitLoader extends GitLoader {
        public String formatDate(int time, String timeZone) {
            return super.formatDate(time, timeZone);
        }
        protected String determineEncoding(byte[] array) {
            return super.determineEncoding(array);
        }
    }

    public void testFormatDate() {
        assertEquals("2007-09-16 10:18:10 GMT", new TestGitLoader().formatDate(1189937890, "GMT"));
    }
    
    public void testDetermineEncoding() {
        assertNull(new TestGitLoader().determineEncoding(new byte[] {}));
        assertNull(new TestGitLoader().determineEncoding(new byte[] {(byte)0xEF, (byte)0xBB}));
        assertEquals("UTF-8", new TestGitLoader().determineEncoding(new byte[] {(byte)0xEF, (byte)0xBB, 0}));
        assertEquals("UTF-16", new TestGitLoader().determineEncoding(new byte[] {(byte)0xFE, (byte)0xFF, 0}));
        assertEquals("UTF-16", new TestGitLoader().determineEncoding(new byte[] {(byte)0xFF, (byte)0xFE, 0}));
        assertNull(new TestGitLoader().determineEncoding(new byte[] {(byte)0xFF, (byte)0xEE, 0}));
        
    }

}
