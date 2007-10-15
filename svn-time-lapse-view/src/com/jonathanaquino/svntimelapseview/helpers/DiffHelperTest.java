package com.jonathanaquino.svntimelapseview.helpers;

import org.apache.commons.lang.StringUtils;

import com.jonathanaquino.svntimelapseview.Diff;

import junit.framework.TestCase;

public class DiffHelperTest extends TestCase {

	private class TestDiffHelper extends DiffHelper {
		public String[] mySplit(String fileContents) {
			return split(fileContents);
		}
	}
	
	public void testSplit() {
		assertEquals(join(new String[] {"a", "b", "c"}), join(new TestDiffHelper().mySplit("a\nb\nc")));
		assertEquals(join(new String[] {"a", "b", "c"}), join(new TestDiffHelper().mySplit("a\rb\rc")));
		assertEquals(join(new String[] {"a", "b", "c"}), join(new TestDiffHelper().mySplit("a\r\nb\r\nc")));
		assertEquals(join(new String[] {"a", "", "b", "", "c"}), join(new TestDiffHelper().mySplit("a\n\rb\n\rc")));
	}
	
	public void testDiff() {
		Diff diff = DiffHelper.diff("a\nb\nc\n", "a\nc\n");
		assertEquals("1 a\n2 b\n3 c\n", diff.getLeftText());
		assertEquals("1 a\n  \n2 c\n", diff.getRightText());
		
		diff = DiffHelper.diff("a\nc\n", "a\nb\nc\n");		
		assertEquals("1 a\n  \n2 c\n", diff.getLeftText());
		assertEquals("1 a\n2 b\n3 c\n", diff.getRightText());
		
		diff = DiffHelper.diff("a\nb\nc\n", "a\nb\n");		
		assertEquals("1 a\n2 b\n3 c\n", diff.getLeftText());
		assertEquals("1 a\n2 b\n  \n", diff.getRightText());
		
		diff = DiffHelper.diff("a\nb\n", "a\nb\nc\n");
		assertEquals("1 a\n2 b\n  \n", diff.getLeftText());
		assertEquals("1 a\n2 b\n3 c\n", diff.getRightText());
		
		diff = DiffHelper.diff("a\nb\nc\n", "b\nc\n");		
		assertEquals("1 a\n2 b\n3 c\n", diff.getLeftText());
		assertEquals("  \n1 b\n2 c\n", diff.getRightText());
		
		diff = DiffHelper.diff("b\nc\n", "a\nb\nc\n");
		assertEquals("  \n1 b\n2 c\n", diff.getLeftText());
		assertEquals("1 a\n2 b\n3 c\n", diff.getRightText());		
	}

	private String join(String[] strings) {
		return StringUtils.join(strings, ", ");
	}

}
