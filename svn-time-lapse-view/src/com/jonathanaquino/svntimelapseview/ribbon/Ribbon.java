package com.jonathanaquino.svntimelapseview.ribbon;

/**
 * A panel for the interactive status line at the bottom of the application window.
 */
public interface Ribbon {

	/**
	 * Called when a Ribbon is displayed. May be called more than once.
	 */
	public void initialize();
	
}
