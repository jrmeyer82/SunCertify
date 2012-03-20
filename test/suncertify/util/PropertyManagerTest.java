package suncertify.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import suncertify.PropertyManager;

public class PropertyManagerTest {
	
	PropertyManager propMan; 
	
	@Before
	public void setUp() {
		propMan = PropertyManager.getInstance();
	}
	
	@Test
	public void testGetProperties() {
		assertTrue(propMan.getProperties().contains("database.file.path"));		
	}
	
	@Test
	public void testContainsProperty() {
		//assertTrue(propMan.containsProperty("test.property"));
		assertTrue(propMan.containsProperty("database.file.path"));
		assertFalse(propMan.containsProperty("negative.test"));
	}
	
	/*@Test
	public void testGetProperty() {
		assertTrue(propMan.getProperty("database.file.path").startsWith("C:\\"));
	}*/
	
}
