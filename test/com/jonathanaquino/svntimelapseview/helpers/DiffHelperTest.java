package com.jonathanaquino.svntimelapseview.helpers;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;

import com.jonathanaquino.svntimelapseview.Diff;

public class DiffHelperTest extends TestCase {

	private class TestDiffHelper extends DiffHelper {
		public String[] mySplit(String fileContents) {
			return split(fileContents);
		}
	}

	public void testSplit() {
		assertEquals(StringUtils.join((new String[] {"a", "b", "c"}), ", "), StringUtils.join(new TestDiffHelper().mySplit("a\nb\nc"), ", "));
		assertEquals(StringUtils.join((new String[] {"a", "b", "c"}), ", "), StringUtils.join(new TestDiffHelper().mySplit("a\rb\rc"), ", "));
		assertEquals(StringUtils.join((new String[] {"a", "b", "c"}), ", "), StringUtils.join(new TestDiffHelper().mySplit("a\r\nb\r\nc"), ", "));
		assertEquals(StringUtils.join((new String[] {"a", "", "b", "", "c"}), ", "), StringUtils.join(new TestDiffHelper().mySplit("a\n\rb\n\rc"), ", "));
	}

	public void testDiff() {
		Diff diff = DiffHelper.diff("a\nb\nc\n", "a\nc\n");
		assertEquals("1 a\n2 b\n3 c\n", diff.getLeftText());
		assertEquals("1 a\n  \n2 c\n", diff.getRightText());
		assertEquals("1", StringUtils.join(diff.getDifferencePositions(), ","));

		diff = DiffHelper.diff("a\nc\n", "a\nb\nc\n");
		assertEquals("1 a\n  \n2 c\n", diff.getLeftText());
		assertEquals("1 a\n2 b\n3 c\n", diff.getRightText());
		assertEquals("1", StringUtils.join(diff.getDifferencePositions(), ","));

		diff = DiffHelper.diff("a\nb\nc\n", "a\nb\n");
		assertEquals("1 a\n2 b\n3 c\n", diff.getLeftText());
		assertEquals("1 a\n2 b\n  \n", diff.getRightText());
		assertEquals("2", StringUtils.join(diff.getDifferencePositions(), ","));

		diff = DiffHelper.diff("a\nb\n", "a\nb\nc\n");
		assertEquals("1 a\n2 b\n  \n", diff.getLeftText());
		assertEquals("1 a\n2 b\n3 c\n", diff.getRightText());
		assertEquals("2", StringUtils.join(diff.getDifferencePositions(), ","));

		diff = DiffHelper.diff("a\nb\nc\n", "b\nc\n");
		assertEquals("1 a\n2 b\n3 c\n", diff.getLeftText());
		assertEquals("  \n1 b\n2 c\n", diff.getRightText());
		assertEquals("0", StringUtils.join(diff.getDifferencePositions(), ","));

		diff = DiffHelper.diff("b\nc\n", "a\nb\nc\n");
		assertEquals("  \n1 b\n2 c\n", diff.getLeftText());
		assertEquals("1 a\n2 b\n3 c\n", diff.getRightText());
		assertEquals("0", StringUtils.join(diff.getDifferencePositions(), ","));

		diff = DiffHelper.diff("A\nA\nA\nA\nA\nA\n", "0\n1\n2\n3\n4\n\5\n");
		assertEquals("0", StringUtils.join(diff.getDifferencePositions(), ","));

		diff = DiffHelper.diff("A\nA\nA\nA\nA\nA\n", "A\n1\n2\nA\nA\n\5\n");
		assertEquals("1,5", StringUtils.join(diff.getDifferencePositions(), ","));
	}

}
