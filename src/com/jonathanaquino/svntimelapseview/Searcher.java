package com.jonathanaquino.svntimelapseview;

/**
 * Searches the text of the two revisions being compared.
 */
public class Searcher {

	/** The text of the left revision. */
	private String leftText;
	
	/** The text of the right revision. */
	private String rightText;
	
	/** The position at which to begin the next search. */
	private int pointer = 0;
	
	/**
	 * Whether the search result is on the left revision or the right.
	 */
	public static class Side {
		private Side() { }
	}
		
	/** The left revision */
    public static final Side LEFT = new Side();
    
    /** The right revision */
    public static final Side RIGHT = new Side();    

	/**
	 * Creates a new Searcher.
	 * 
	 * @param leftText  the text of the left revision
	 * @param rightText  the text of the right revision
	 */
	public Searcher(String leftText, String rightText) {
		this.leftText = leftText;
		this.rightText = rightText;
	}
	
	/**
	 * Searches for the given text.
	 * 
	 * @param s  the text to search for
	 * @return  whether the text was found
	 */
	public boolean search(String s) {
		if (s.length() == 0) { return false; }
		String combinedText = (leftText + rightText).toLowerCase();
		int i = combinedText.indexOf(s.toLowerCase(), pointer);
		if (i != -1) {
			pointer = i + 1;
			if (pointer >= combinedText.length()) { pointer = 0; }
			return true;
		}
		i = combinedText.indexOf(s.toLowerCase());
		if (i != -1 && i < pointer) {
			pointer = i + 1;
			if (pointer >= combinedText.length()) { pointer = 0; }
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the revision of the last search.
	 * 
	 * @return  whether the search result is on the left revision or the right
	 */
	public Side getSide() {
		int position = pointer - 1;
		if (position == -1) { return RIGHT; }
		if (position < leftText.length()) { return LEFT; }
		return RIGHT;
	}
	
	/**
	 * Returns the zero-based position of the last search.
	 * 
	 * @return  the index of the search result within its revision (left or right)
	 */
	public int getPosition() {
		int position = pointer - 1;
		if (position == -1) { return rightText.length() - 1; }
		if (position < leftText.length()) { return position; }
		return position - leftText.length();		
	}
	
}
