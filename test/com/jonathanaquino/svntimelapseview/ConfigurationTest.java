package com.jonathanaquino.svntimelapseview;

import java.io.File;

import junit.framework.TestCase;

public class ConfigurationTest extends TestCase {

	private Configuration configuration;

	protected void setUp() throws Exception {
		configuration = new Configuration("configuration_test.txt");
	}

	public void testGetInt() throws Exception {
		assertEquals(5, configuration.getInt("x", 5));
		configuration.setInt("x", 6);
		assertEquals(6, configuration.getInt("x", 5));
	}

	public void testGetBoolean() throws Exception {
		assertEquals(true, configuration.getBoolean("x", true));
		configuration.setBoolean("x", false);
		assertEquals(false, configuration.getBoolean("x", true));
	}

	private class TestConfiguration extends Configuration {
		public TestConfiguration(String filePath) throws Exception { super(filePath); }
		public boolean filePathSpecified() { return super.filePathSpecified(); }
	}

	public void testFilePathSpecified() throws Exception {
		assertFalse(new TestConfiguration(null).filePathSpecified());
		assertFalse(new TestConfiguration("").filePathSpecified());
		assertTrue(new TestConfiguration("x").filePathSpecified());
	}

	protected void tearDown() throws Exception {
		new File("configuration_test.txt").delete();
	}

}
