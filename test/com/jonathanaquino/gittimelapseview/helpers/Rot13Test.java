package com.jonathanaquino.gittimelapseview.helpers;

import com.jonathanaquino.gittimelapseview.helpers.Rot13;

import junit.framework.TestCase;

public class Rot13Test extends TestCase {

    public void testRot13() {
        assertEquals("sbb", Rot13.rot13("foo"));
        assertEquals("foo", Rot13.rot13(Rot13.rot13("foo")));
    }

}
