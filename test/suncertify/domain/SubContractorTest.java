package suncertify.domain;

import static org.junit.Assert.*;

import org.junit.Test;


public class SubContractorTest {
	
	@Test
	public void testEquals() {
		SubContractorHandler scHandler = new SubContractorHandler();
		SubContractor sc1 = scHandler.createSubContractor(1, new String[]{null, "Ort", "Spec", "10", "10.0", null});
		SubContractor sc2 = scHandler.createSubContractor(2, new String[]{null, "Ort", "Spec", "10", "10.0", null});
		SubContractor sc3 = scHandler.createSubContractor(3, new String[]{"HP", "Ort", "Spec", "10", "10.0", null});
		assertEquals(sc1, sc2);
		assertTrue(sc1.equals(sc2));
		assertFalse(sc1.equals(sc3));
		assertFalse(sc2.equals(sc3));
	}

}
