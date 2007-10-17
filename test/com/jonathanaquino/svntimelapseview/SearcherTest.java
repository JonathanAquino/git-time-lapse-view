package com.jonathanaquino.svntimelapseview;

import junit.framework.TestCase;

public class SearcherTest extends TestCase {

	public void testSearch() {
		Searcher searcher = new Searcher("A1B2A3B4A5", "A6B7A8B9A.");

		assertTrue(searcher.search("A"));
		assertEquals(Searcher.LEFT, searcher.getSide());
		assertEquals(0, searcher.getPosition());

		assertTrue(searcher.search("A"));
		assertEquals(Searcher.LEFT, searcher.getSide());
		assertEquals(4, searcher.getPosition());

		assertTrue(searcher.search("A"));
		assertEquals(Searcher.LEFT, searcher.getSide());
		assertEquals(8, searcher.getPosition());

		assertTrue(searcher.search("A"));
		assertEquals(Searcher.RIGHT, searcher.getSide());
		assertEquals(0, searcher.getPosition());

		assertTrue(searcher.search("A"));
		assertEquals(Searcher.RIGHT, searcher.getSide());
		assertEquals(4, searcher.getPosition());

		assertTrue(searcher.search("A"));
		assertEquals(Searcher.RIGHT, searcher.getSide());
		assertEquals(8, searcher.getPosition());

		assertTrue(searcher.search("A"));
		assertEquals(Searcher.LEFT, searcher.getSide());
		assertEquals(0, searcher.getPosition());

		assertTrue(searcher.search("A"));
		assertEquals(Searcher.LEFT, searcher.getSide());
		assertEquals(4, searcher.getPosition());

		assertTrue(searcher.search("A"));
		assertEquals(Searcher.LEFT, searcher.getSide());
		assertEquals(8, searcher.getPosition());

		assertTrue(searcher.search("A"));
		assertEquals(Searcher.RIGHT, searcher.getSide());
		assertEquals(0, searcher.getPosition());

		assertTrue(searcher.search("."));
		assertEquals(Searcher.RIGHT, searcher.getSide());
		assertEquals(9, searcher.getPosition());

		assertTrue(searcher.search("."));
		assertEquals(Searcher.RIGHT, searcher.getSide());
		assertEquals(9, searcher.getPosition());

		assertTrue(searcher.search("A"));
		assertEquals(Searcher.LEFT, searcher.getSide());
		assertEquals(0, searcher.getPosition());

		assertFalse(searcher.search(""));
	}

	public void testSearch2() {
		Searcher searcher = new Searcher("ABC", "DEF");
		assertTrue(searcher.search("A"));
		assertEquals(Searcher.LEFT, searcher.getSide());
		assertEquals(0, searcher.getPosition());
		assertTrue(searcher.search("A"));
		assertEquals(Searcher.LEFT, searcher.getSide());
		assertEquals(0, searcher.getPosition());

		searcher = new Searcher("ABC", "DEF");
		assertTrue(searcher.search("B"));
		assertEquals(Searcher.LEFT, searcher.getSide());
		assertEquals(1, searcher.getPosition());
		assertTrue(searcher.search("B"));
		assertEquals(Searcher.LEFT, searcher.getSide());
		assertEquals(1, searcher.getPosition());

		searcher = new Searcher("ABC", "DEF");
		assertTrue(searcher.search("C"));
		assertEquals(Searcher.LEFT, searcher.getSide());
		assertEquals(2, searcher.getPosition());
		assertTrue(searcher.search("C"));
		assertEquals(Searcher.LEFT, searcher.getSide());
		assertEquals(2, searcher.getPosition());

		searcher = new Searcher("ABC", "DEF");
		assertTrue(searcher.search("D"));
		assertEquals(Searcher.RIGHT, searcher.getSide());
		assertEquals(0, searcher.getPosition());
		assertTrue(searcher.search("D"));
		assertEquals(Searcher.RIGHT, searcher.getSide());
		assertEquals(0, searcher.getPosition());

		searcher = new Searcher("ABC", "DEF");
		assertTrue(searcher.search("E"));
		assertEquals(Searcher.RIGHT, searcher.getSide());
		assertEquals(1, searcher.getPosition());
		assertTrue(searcher.search("E"));
		assertEquals(Searcher.RIGHT, searcher.getSide());
		assertEquals(1, searcher.getPosition());

		searcher = new Searcher("ABC", "DEF");
		assertTrue(searcher.search("F"));
		assertEquals(Searcher.RIGHT, searcher.getSide());
		assertEquals(2, searcher.getPosition());
		assertTrue(searcher.search("F"));
		assertEquals(Searcher.RIGHT, searcher.getSide());
		assertEquals(2, searcher.getPosition());
	}

}
