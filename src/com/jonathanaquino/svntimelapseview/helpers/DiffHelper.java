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
	 * @return  the lines that differ
	 */
	public static Diff diff(String leftFileContents, String rightFileContents) {
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
		StringBuffer leftText = new StringBuffer();		
		StringBuffer rightText = new StringBuffer();
		StringBuffer leftHtml = new StringBuffer();		
		StringBuffer rightHtml = new StringBuffer();
		List leftLines = new ArrayList();
		List rightLines = new ArrayList();
		int lineNumberWidth = String.valueOf(leftLineNumbers.size()).length() + 1;
		for (int i = 0; i < leftLineNumbers.size(); i++) {
			String leftLineNumber = leftLineNumbers.get(i).toString();
			String rightLineNumber = rightLineNumbers.get(i).toString();
			String leftLine = leftLineNumber.length() == 0 ? "" : leftFileLines[Integer.parseInt(leftLineNumber)-1];
			String rightLine = rightLineNumber.length() == 0 ? "" : rightFileLines[Integer.parseInt(rightLineNumber)-1];
			leftLines.add(leftLine);
			rightLines.add(rightLine);
			leftText.append(StringUtils.rightPad(leftLineNumber, lineNumberWidth)).append(leftLine).append('\n');
			rightText.append(StringUtils.rightPad(rightLineNumber, lineNumberWidth)).append(rightLine).append('\n');
			String[] html = html(leftLineNumber, rightLineNumber, leftLine, rightLine);
			// Make sure the anchor tag is not empty; otherwise the Highlight offsets seem to get messed up [Jon Aquino 2007-10-16]
			leftHtml.append("<a name='Position" + i + "'>").append(StringUtils.rightPad(leftLineNumber, lineNumberWidth)).append("</a>").append(html[0]).append('\n');;
			rightHtml.append("<a name='Position" + i + "'>").append(StringUtils.rightPad(rightLineNumber, lineNumberWidth)).append("</a>").append(html[1]).append('\n');;
			
		}
		return new Diff("<pre>" + leftHtml + "</pre>", "<pre>" + rightHtml + "</pre>", leftText.toString(), rightText.toString(), differencePositions(leftLines, rightLines));
	}
	
	/**
	 * Returns the zero-based line numbers at which differences start.
	 * 
	 * @param leftLines  text for the left side of the diff
	 * @param rightLines  text for the right side of the diff
	 * @return  0 for the first line, 1 for the 2nd, etc.
	 */
	private static List differencePositions(List leftLines, List rightLines) {
		List differencePositions = new ArrayList();
		boolean insideDifference = false;
		for (int i = 0; i < leftLines.size(); i++) {
			if (leftLines.get(i).equals(rightLines.get(i))) {
				insideDifference = false;
				continue; 
			}
			if (! insideDifference) {
				insideDifference = true;
				differencePositions.add(new Integer(i));
			}
		}
		return differencePositions;
	}

	/**
	 * Returns HTML for the two lines
	 * 
	 * @param leftLineNumber  line number for the left line, or an empty string if it does not exist
	 * @param rightLineNumber line number for the right line, or an empty string if it does not exist 
	 * @param leftLine  the left line
	 * @param rightLine  the right line
	 * @return  two HTML strings
	 */
	private static String[] html(String leftLineNumber, String rightLineNumber, String leftLine, String rightLine) {
		String leftOpeningTag = "", leftClosingTag = "", rightOpeningTag = "", rightClosingTag = "";
		if (leftLineNumber.length() == 0) { 
			rightOpeningTag = "<span style='background-color: #A6CAF0'>";
			rightClosingTag = "</span>";
		} else if (rightLineNumber.length() == 0) {  
			leftOpeningTag = "<span style='background-color: #A6CAF0'>";
			leftClosingTag = "</span>";
		} else if (! leftLine.equals(rightLine)) {
			leftOpeningTag = rightOpeningTag = "<span style='background-color: #A6CAF0'>";
			leftClosingTag = rightClosingTag = "</span>";			
		}
		return new String[] { leftOpeningTag + StringEscapeUtils.escapeHtml(leftLine) + leftClosingTag, rightOpeningTag + StringEscapeUtils.escapeHtml(rightLine) + rightClosingTag };
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
