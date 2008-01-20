package com.jonathanaquino.svntimelapseview.helpers;

/* Copyright 2002, 2003 Elliotte Rusty Harold

This library is free software; you can redistribute it and/or modify
it under the terms of version 2.1 of the GNU Lesser General Public
License as published by the Free Software Foundation.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place, Suite 330,
Boston, MA 02111-1307  USA

You can contact Elliotte Rusty Harold by sending e-mail to
elharo@metalab.unc.edu. Please include the word "XOM" in the
subject line. The XOM home page is located at http://www.xom.nu/
*/

/**
* <p>
*   Demonstrates getter and setter methods in the <code>Text</code> class,
*   as well as recursive descent through a document.
* </p>
*
* @author Elliotte Rusty Harold
* @version 1.0
*
*/
public class Rot13 {
    
    // From nu.xom.samples.ROT13XML [Jon Aquino 2008-01-19]

    public static String rot13(String s) {
        StringBuffer out = new StringBuffer(s.length());
        for (int i = 0; i < s.length(); i++) {
            int c = s.charAt(i);
            if (c >= 'A' && c <= 'M') out.append((char) (c+13));
            else if (c >= 'N' && c <= 'Z') out.append((char) (c-13));
            else if (c >= 'a' && c <= 'm') out.append((char) (c+13));
            else if (c >= 'n' && c <= 'z') out.append((char) (c-13));
            else out.append((char) c);
        }
        return out.toString();

    }

}