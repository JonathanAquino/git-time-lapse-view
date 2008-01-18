package com.jonathanaquino.svntimelapseview;

import junit.framework.TestCase;

public class SearchPanelTest extends TestCase {

    private class TestSearchPanel extends SearchPanel {
    }

    public void testNextDiffIndex() {
        TestSearchPanel searchPanel = new TestSearchPanel();
        assertEquals(1, searchPanel.nextDiffIndex(0, 3));
        assertEquals(2, searchPanel.nextDiffIndex(1, 3));
        assertEquals(2, searchPanel.nextDiffIndex(2, 3));
        assertEquals(0, searchPanel.previousDiffIndex(0, 3));
        assertEquals(0, searchPanel.previousDiffIndex(1, 3));
        assertEquals(1, searchPanel.previousDiffIndex(2, 3));
        assertEquals(0, searchPanel.nextDiffIndex(-1, 3));
        assertEquals(0, searchPanel.previousDiffIndex(-1, 3));
    }

}
