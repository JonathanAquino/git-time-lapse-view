package com.jonathanaquino.svntimelapseview;

/**
 * The set of differences between two text files.
 */
public class Diff {

	/** HTML for the left side of the diff */
	private String leftHtml;
	
	/** HTML for the right side of the diff */
	private String rightHtml;
	
	/** Text for the left side of the diff */
	private String leftText;
	
	/** Text for the right side of the diff */
	private String rightText;
	
	/**
	 * Creates a new Diff.
	 *
	 * @param leftHtml  HTML for the left side of the diff
	 * @param rightHtml  HTML for the right side of the diff
	 * @param leftText  text for the left side of the diff
	 * @param rightText  text for the right side of the diff
	 */
	public Diff(String leftHtml, String rightHtml, String leftText, String rightText) {
		this.leftHtml = leftHtml;
		this.rightHtml = rightHtml;
		this.leftText = leftText;
		this.rightText = rightText;
	}
	
	/**
	 * Returns the HTML for the left side of the diff.
	 * 
	 * @return  the plain-HTML version, used by the unit tests
	 */
	public String getLeftHtml() {
		return leftHtml;
	}

	/**
	 * Returns the HTML for the right side of the diff.
	 * 
	 * @return  the plain-HTML version, used by the unit tests
	 */
	public String getRightHtml() {
		return rightHtml;
	}

	/**
	 * Returns the text for the left side of the diff.
	 * 
	 * @return  the plain-text version, used by the unit tests
	 */
	public String getLeftText() {
		return leftText;
	}

	/**
	 * Returns the text for the right side of the diff.
	 * 
	 * @return  the plain-text version, used by the unit tests
	 */
	public String getRightText() {
		return rightText;
	}
	
	
}
