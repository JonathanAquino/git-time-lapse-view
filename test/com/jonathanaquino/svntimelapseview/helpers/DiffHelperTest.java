package com.jonathanaquino.svntimelapseview.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;

import com.jonathanaquino.svntimelapseview.Diff;

public class DiffHelperTest extends TestCase {

	private class TestDiffHelper extends DiffHelper {
		public String[] mySplit(String fileContents) {
			return split(fileContents);
		}
		public void myKeepDifferencesOnly(int context, boolean[] differenceFlags, List leftLines, List rightLines, List leftLineNumbers, List rightLineNumbers) {
			keepDifferencesOnly(context, differenceFlags, leftLines, rightLines, leftLineNumbers, rightLineNumbers);
		}
	}
	
	public void testKeepDifferencesOnly1() {
		boolean[] differenceFlags = new boolean[] {};
		List leftLines = new ArrayList(Arrays.asList(new String[] {}));
		List rightLines = new ArrayList(Arrays.asList(new String[] {}));
		List leftLineNumbers = new ArrayList(Arrays.asList(new String[] {}));
		List rightLineNumbers = new ArrayList(Arrays.asList(new String[] {}));
		new TestDiffHelper().myKeepDifferencesOnly(2, differenceFlags, leftLines, rightLines, leftLineNumbers, rightLineNumbers);
		assertEquals(";;;", StringUtils.join(leftLines, ",") + ";" + StringUtils.join(rightLines, ",") + ";" + StringUtils.join(leftLineNumbers, ",") + ";" + StringUtils.join(rightLineNumbers, ","));
	}
	
	public void testKeepDifferencesOnly2() {
		boolean[] differenceFlags = new boolean[] {true, false, false, false, false, false};
		List leftLines = new ArrayList(Arrays.asList(new String[] {"a", "b", "c", "d", "e", "f"}));
		List rightLines = new ArrayList(Arrays.asList(new String[] {"", "b", "c", "d", "e", "f"}));
		List leftLineNumbers = new ArrayList(Arrays.asList(new String[] {"1", "2", "3", "4", "5", "6"}));
		List rightLineNumbers = new ArrayList(Arrays.asList(new String[] {"", "1", "2", "3", "4", "5"}));
		new TestDiffHelper().myKeepDifferencesOnly(2, differenceFlags, leftLines, rightLines, leftLineNumbers, rightLineNumbers);
		assertEquals("a,b,c,,,;,b,c,,,;1,2,3,,,;,1,2,,,", StringUtils.join(leftLines, ",") + ";" + StringUtils.join(rightLines, ",") + ";" + StringUtils.join(leftLineNumbers, ",") + ";" + StringUtils.join(rightLineNumbers, ","));
	}
	
	public void testKeepDifferencesOnly3() {
		boolean[] differenceFlags = new boolean[] {false, false, false, false, false, true};
		List leftLines = new ArrayList(Arrays.asList(new String[] {"a", "b", "c", "d", "e", "f"}));
		List rightLines = new ArrayList(Arrays.asList(new String[] {"a", "b", "c", "d", "e", ""}));
		List leftLineNumbers = new ArrayList(Arrays.asList(new String[] {"1", "2", "3", "4", "5", "6"}));
		List rightLineNumbers = new ArrayList(Arrays.asList(new String[] {"1", "2", "3", "4", "5", ""}));
		new TestDiffHelper().myKeepDifferencesOnly(2, differenceFlags, leftLines, rightLines, leftLineNumbers, rightLineNumbers);
		assertEquals("d,e,f;d,e,;4,5,6;4,5,", StringUtils.join(leftLines, ",") + ";" + StringUtils.join(rightLines, ",") + ";" + StringUtils.join(leftLineNumbers, ",") + ";" + StringUtils.join(rightLineNumbers, ","));
	}
	
	public void testKeepDifferencesOnly4() {
		boolean[] differenceFlags = new boolean[] {true, false, false, false, false, true};
		List leftLines = new ArrayList(Arrays.asList(new String[] {"", "b", "c", "d", "e", ""}));
		List rightLines = new ArrayList(Arrays.asList(new String[] {"a", "b", "c", "d", "e", "f"}));
		List leftLineNumbers = new ArrayList(Arrays.asList(new String[] {"", "1", "2", "3", "4", ""}));
		List rightLineNumbers = new ArrayList(Arrays.asList(new String[] {"1", "2", "3", "4", "5", "6"}));
		new TestDiffHelper().myKeepDifferencesOnly(1, differenceFlags, leftLines, rightLines, leftLineNumbers, rightLineNumbers);
		assertEquals(",b,,,,e,;a,b,,,,e,f;,1,,,,4,;1,2,,,,5,6", StringUtils.join(leftLines, ",") + ";" + StringUtils.join(rightLines, ",") + ";" + StringUtils.join(leftLineNumbers, ",") + ";" + StringUtils.join(rightLineNumbers, ","));
	}
	
	public void testKeepDifferencesOnly5() {
		boolean[] differenceFlags = new boolean[] {false, false, true, false, false, false};
		List leftLines = new ArrayList(Arrays.asList(new String[] {"a", "b", "X", "d", "e", "f"}));
		List rightLines = new ArrayList(Arrays.asList(new String[] {"a", "b", "c", "d", "e", "f"}));
		List leftLineNumbers = new ArrayList(Arrays.asList(new String[] {"1", "2", "3", "4", "5", "6"}));
		List rightLineNumbers = new ArrayList(Arrays.asList(new String[] {"1", "2", "3", "4", "5", "6"}));
		new TestDiffHelper().myKeepDifferencesOnly(1, differenceFlags, leftLines, rightLines, leftLineNumbers, rightLineNumbers);
		assertEquals("b,X,d,,,;b,c,d,,,;2,3,4,,,;2,3,4,,,", StringUtils.join(leftLines, ",") + ";" + StringUtils.join(rightLines, ",") + ";" + StringUtils.join(leftLineNumbers, ",") + ";" + StringUtils.join(rightLineNumbers, ","));
	}
	
	public void testKeepDifferencesOnly6() {
		boolean[] differenceFlags = new boolean[] {false, false, true, true, false, false};
		List leftLines = new ArrayList(Arrays.asList(new String[] {"a", "b", "X", "X", "e", "f"}));
		List rightLines = new ArrayList(Arrays.asList(new String[] {"a", "b", "c", "d", "e", "f"}));
		List leftLineNumbers = new ArrayList(Arrays.asList(new String[] {"1", "2", "3", "4", "5", "6"}));
		List rightLineNumbers = new ArrayList(Arrays.asList(new String[] {"1", "2", "3", "4", "5", "6"}));
		new TestDiffHelper().myKeepDifferencesOnly(1, differenceFlags, leftLines, rightLines, leftLineNumbers, rightLineNumbers);
		assertEquals("b,X,X,e,,,;b,c,d,e,,,;2,3,4,5,,,;2,3,4,5,,,", StringUtils.join(leftLines, ",") + ";" + StringUtils.join(rightLines, ",") + ";" + StringUtils.join(leftLineNumbers, ",") + ";" + StringUtils.join(rightLineNumbers, ","));
	}	

	public void testSplit() {
		assertEquals(StringUtils.join((new String[] {"a", "b", "c"}), ", "), StringUtils.join(new TestDiffHelper().mySplit("a\nb\nc"), ", "));
		assertEquals(StringUtils.join((new String[] {"a", "b", "c"}), ", "), StringUtils.join(new TestDiffHelper().mySplit("a\rb\rc"), ", "));
		assertEquals(StringUtils.join((new String[] {"a", "b", "c"}), ", "), StringUtils.join(new TestDiffHelper().mySplit("a\r\nb\r\nc"), ", "));
		assertEquals(StringUtils.join((new String[] {"a", "", "b", "", "c"}), ", "), StringUtils.join(new TestDiffHelper().mySplit("a\n\rb\n\rc"), ", "));
	}

	public void testDiff() {
		Diff diff = DiffHelper.diff("a\nb\nc", "a\nc", false);
		assertEquals("1 a\n2 b\n3 c", diff.getLeftText());
		assertEquals("1 a\n  \n2 c", diff.getRightText());
		assertEquals("1", StringUtils.join(diff.getDifferencePositions(), ","));

		diff = DiffHelper.diff("a\nc", "a\nb\nc", false);
		assertEquals("1 a\n  \n2 c", diff.getLeftText());
		assertEquals("1 a\n2 b\n3 c", diff.getRightText());
		assertEquals("1", StringUtils.join(diff.getDifferencePositions(), ","));

		diff = DiffHelper.diff("a\nb\nc", "a\nb", false);
		assertEquals("1 a\n2 b\n3 c", diff.getLeftText());
		assertEquals("1 a\n2 b\n  ", diff.getRightText());
		assertEquals("2", StringUtils.join(diff.getDifferencePositions(), ","));

		diff = DiffHelper.diff("a\nb", "a\nb\nc", false);
		assertEquals("1 a\n2 b\n  ", diff.getLeftText());
		assertEquals("1 a\n2 b\n3 c", diff.getRightText());
		assertEquals("2", StringUtils.join(diff.getDifferencePositions(), ","));

		diff = DiffHelper.diff("a\nb\nc", "b\nc", false);
		assertEquals("1 a\n2 b\n3 c", diff.getLeftText());
		assertEquals("  \n1 b\n2 c", diff.getRightText());
		assertEquals("0", StringUtils.join(diff.getDifferencePositions(), ","));

		diff = DiffHelper.diff("b\nc", "a\nb\nc", false);
		assertEquals("  \n1 b\n2 c", diff.getLeftText());
		assertEquals("1 a\n2 b\n3 c", diff.getRightText());
		assertEquals("0", StringUtils.join(diff.getDifferencePositions(), ","));

		diff = DiffHelper.diff("A\nA\nA\nA\nA\nA", "0\n1\n2\n3\n4\n\5", false);
		assertEquals("0", StringUtils.join(diff.getDifferencePositions(), ","));

		diff = DiffHelper.diff("A\nA\nA\nA\nA\nA", "A\n1\n2\nA\nA\n\5", false);
		assertEquals("1,5", StringUtils.join(diff.getDifferencePositions(), ","));
	}

}
