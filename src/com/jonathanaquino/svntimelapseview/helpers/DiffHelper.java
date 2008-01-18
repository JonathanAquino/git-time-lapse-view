package com.jonathanaquino.svntimelapseview.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.incava.util.diff.Difference;

import com.jonathanaquino.svntimelapseview.Diff;

/**
 * Utility functions for diffing files.
 */
public class DiffHelper {

	/**
	 * Returns a diff of two text files
	 *
	 * @param leftFileContents  the contents of the first file
	 * @param rightFileContents  the contents of the second file
	 * @param showDifferencesOnly  whether to hide identical lines
	 * @return  the lines that differ
	 */
	public static Diff diff(String leftFileContents, String rightFileContents, boolean showDifferencesOnly) {
		String[] leftFileLines = split(leftFileContents);
		String[] rightFileLines = split(rightFileContents);
		List leftLineNumbers = lineNumbers(leftFileLines);
		List rightLineNumbers = lineNumbers(rightFileLines);
		List differences = new org.incava.util.diff.Diff(leftFileLines, rightFileLines).diff();
		Collections.reverse(differences);
		for (Iterator i = differences.iterator(); i.hasNext(); ) {
			Difference difference = (Difference) i.next();
			int leftStart = difference.getDeletedStart();
			int leftEnd = difference.getDeletedEnd() == Difference.NONE ? leftStart : difference.getDeletedEnd() + 1;
			int rightStart = difference.getAddedStart();
			int rightEnd = difference.getAddedEnd() == Difference.NONE ? rightStart : difference.getAddedEnd() + 1;
			int leftExtraLineCount = (leftEnd-leftStart) - (rightEnd-rightStart);
			if (leftExtraLineCount > 0) { rightLineNumbers.subList(rightEnd, rightEnd).addAll(Collections.nCopies(leftExtraLineCount, "")); }
			int rightExtraLineCount = (rightEnd-rightStart) - (leftEnd-leftStart);
			if (rightExtraLineCount > 0) { leftLineNumbers.subList(leftEnd, leftEnd).addAll(Collections.nCopies(rightExtraLineCount, "")); }
		}
		if (leftLineNumbers.size() != rightLineNumbers.size()) { throw new RuntimeException("Assertion failed: " + leftLineNumbers.size() + " != " + rightLineNumbers.size()); }
		List leftFormattedText = new ArrayList();
		List rightFormattedText = new ArrayList();
		List leftHtml = new ArrayList();
		List rightHtml = new ArrayList();
		List leftLines = new ArrayList();
		List rightLines = new ArrayList();
		int lineNumberWidth = String.valueOf(leftLineNumbers.size()).length() + 1;
		for (int i = 0; i < leftLineNumbers.size(); i++) {
			String leftLineNumber = leftLineNumbers.get(i).toString();
			String rightLineNumber = rightLineNumbers.get(i).toString();
			leftLines.add(leftLineNumber.length() == 0 ? "" : leftFileLines[Integer.parseInt(leftLineNumber)-1]);
			rightLines.add(rightLineNumber.length() == 0 ? "" : rightFileLines[Integer.parseInt(rightLineNumber)-1]);
		}
		boolean[] differenceFlags = differenceFlags(leftLines, rightLines, leftLineNumbers, rightLineNumbers);
		if (showDifferencesOnly) {
			keepDifferencesOnly(3, differenceFlags, leftLines, rightLines, leftLineNumbers, rightLineNumbers);
			differenceFlags = differenceFlags(leftLines, rightLines, leftLineNumbers, rightLineNumbers);
		}
		for (int i = 0; i < leftLineNumbers.size(); i++) {
			String leftLineNumber = leftLineNumbers.get(i).toString();
			String rightLineNumber = rightLineNumbers.get(i).toString();
			String leftLine = leftLines.get(i).toString();
			String rightLine = rightLines.get(i).toString();
			String[] textPair = text(leftLineNumber, rightLineNumber, leftLine, rightLine, lineNumberWidth);
			String[] htmlPair = html(leftLineNumber, rightLineNumber, leftLine, rightLine, i, lineNumberWidth);
			leftFormattedText.add(textPair[0]);
			rightFormattedText.add(textPair[1]);
			leftHtml.add(htmlPair[0]);
			rightHtml.add(htmlPair[1]);
		}		
		return new Diff("<pre>" + StringUtils.join(leftHtml, "\n") + "</pre>", "<pre>" + StringUtils.join(rightHtml, "\n") + "</pre>", StringUtils.join(leftFormattedText, "\n"), StringUtils.join(rightFormattedText, "\n"), differencePositions(differenceFlags));
	}

	/**
	 * Removes elements from the arrays other than differences and a few lines of context
	 * 
	 * @param context  number of lines to show before and after the difference
	 * @param differenceFlags  an array of flags indicating whether the corresponding lines are different
	 * @param leftLines  text for the left side of the diff
	 * @param rightLines  text for the right side of the diff
	 * @param leftLineNumbers  line number strings, or empty strings where lines are missing
	 * @param rightLineNumbers  line number strings, or empty strings where lines are missing
	 */
	protected static void keepDifferencesOnly(int context, boolean[] differenceFlags, List leftLines, List rightLines, List leftLineNumbers, List rightLineNumbers) {
		boolean[] differenceFlagsWithContext = new boolean[differenceFlags.length]; 
		for (int i = 0; i < differenceFlags.length; i++) {
			if (! differenceFlags[i]) { continue; }
			differenceFlagsWithContext[i] = true;
			for (int j = 0; j <= context; j++) {
				if (i - j >= 0) { differenceFlagsWithContext[i-j] = true; }
				if (i + j < differenceFlags.length) { differenceFlagsWithContext[i+j] = true; }
			}
		}
		List newLeftLines = new ArrayList();
		List newRightLines = new ArrayList();
		List newLeftLineNumbers = new ArrayList();
		List newRightLineNumbers = new ArrayList();
		for (int i = 0; i < differenceFlagsWithContext.length; i++) {
			if (differenceFlagsWithContext[i]) {
				newLeftLines.add(leftLines.get(i));
				newRightLines.add(rightLines.get(i));
				newLeftLineNumbers.add(leftLineNumbers.get(i));
				newRightLineNumbers.add(rightLineNumbers.get(i));
			} else if (i > 0 && differenceFlagsWithContext[i-1]) {
				for (int j = 0; j < 3; j++) {
					newLeftLines.add("");
					newRightLines.add("");
					newLeftLineNumbers.add("");
					newRightLineNumbers.add("");
				}
			}
		}
		leftLines.clear();
		leftLines.addAll(newLeftLines);
		rightLines.clear();
		rightLines.addAll(newRightLines);
		leftLineNumbers.clear();
		leftLineNumbers.addAll(newLeftLineNumbers);
		rightLineNumbers.clear();
		rightLineNumbers.addAll(newRightLineNumbers);
	}

	/**
	 * Returns the zero-based line numbers at which differences start.
	 * 
	 * @param differenceFlags  an array of flags indicating whether the corresponding lines are different
	 * @return  0 for the first line, 1 for the 2nd, etc.
	 */
	private static List differencePositions(boolean[] differenceFlags) {
		List differencePositions = new ArrayList();
		for (int i = 0; i < differenceFlags.length; i++) {
			if (i == 0 && differenceFlags[i]) { differencePositions.add(new Integer(i)); }
			if (i > 0 && differenceFlags[i] && !differenceFlags[i-1]) { differencePositions.add(new Integer(i)); }
		}
		return differencePositions;
	}

	/**
	 * Returns an array of flags indicating whether the corresponding lines are different.
	 *
	 * @param leftLines  text for the left side of the diff
	 * @param rightLines  text for the right side of the diff
	 * @param leftLineNumbers  line number strings, or empty strings where lines are missing
	 * @param rightLineNumbers  line number strings, or empty strings where lines are missing
	 * @return  whether pairs of lines are different
	 */
	private static boolean[] differenceFlags(List leftLines, List rightLines, List leftLineNumbers, List rightLineNumbers) {
		boolean[] differenceFlags = new boolean[leftLines.size()];
		for (int i = 0; i < leftLines.size(); i++) {
			differenceFlags[i] = !leftLines.get(i).equals(rightLines.get(i)) || (leftLineNumbers.get(i).toString().length() > 0 != rightLineNumbers.get(i).toString().length() > 0); 
		}
		return differenceFlags;
	}	
	
	/**
	 * Returns formatted text for the two lines
	 *
	 * @param leftLineNumber  line number for the left line, or an empty string if it does not exist
	 * @param rightLineNumber line number for the right line, or an empty string if it does not exist
	 * @param leftLine  the left line
	 * @param rightLine  the right line
	 * @param lineNumberWidth  the number of characters to pad the line numbers up to
	 * @return  two strings
	 */
	private static String[] text(String leftLineNumber, String rightLineNumber, String leftLine, String rightLine, int lineNumberWidth) {
		return new String[] {
				StringUtils.rightPad(leftLineNumber, lineNumberWidth) + leftLine, 
				StringUtils.rightPad(rightLineNumber, lineNumberWidth) + rightLine};
	}

	/**
	 * Returns HTML for the two lines
	 *
	 * @param leftLineNumber  line number for the left line, or an empty string if it does not exist
	 * @param rightLineNumber line number for the right line, or an empty string if it does not exist
	 * @param leftLine  the left line
	 * @param rightLine  the right line
	 * @param position  the zero-based vertical position of the two lines
	 * @param lineNumberWidth  the number of characters to pad the line numbers up to
	 * @return  two HTML strings
	 */
	private static String[] html(String leftLineNumber, String rightLineNumber, String leftLine, String rightLine, int position, int lineNumberWidth) {
		String leftOpeningTag = "", leftClosingTag = "", rightOpeningTag = "", rightClosingTag = "";
		if (leftLineNumber.length() == 0 && rightLineNumber.length() == 0) {
			
		} else if (leftLineNumber.length() == 0) {
			rightOpeningTag = "<span style='background-color: #A6CAF0'>";
			rightClosingTag = "</span>";
		} else if (rightLineNumber.length() == 0) {
			leftOpeningTag = "<span style='background-color: #A6CAF0'>";
			leftClosingTag = "</span>";
		} else if (! leftLine.equals(rightLine)) {
			leftOpeningTag = rightOpeningTag = "<span style='background-color: #A6CAF0'>";
			leftClosingTag = rightClosingTag = "</span>";
		}
		// Make sure the anchor tag is not empty; otherwise the Highlight offsets seem to get messed up [Jon Aquino 2007-10-16]
		return new String[] { 
				leftOpeningTag + "<a name='Position" + position + "'>" + StringUtils.rightPad(leftLineNumber, lineNumberWidth) + "</a>" + StringEscapeUtils.escapeHtml(leftLine) + leftClosingTag, 
				rightOpeningTag + "<a name='Position" + position + "'>" + StringUtils.rightPad(rightLineNumber, lineNumberWidth) + "</a>" + StringEscapeUtils.escapeHtml(rightLine) + rightClosingTag };
	}

	/**
	 * Line numbers for the file
	 *
	 * @param lines  the lines of the text file
	 * @return  an array of line-number strings
	 */
	private static List lineNumbers(String[] lines) {
		List lineNumbers = new ArrayList();
		for (int i = 1; i <= lines.length; i++) {
			lineNumbers.add(String.valueOf(i));
		}
		return lineNumbers;
	}

	/**
	 * Splits the string at \r, \n, or \r\n.
	 *
	 * @param fileContents  the contents of a text file
	 * @return  the lines of the text file
	 */
	protected static String[] split(String fileContents) {
		return fileContents.split("\r\n|\r|\n");
	}

}
