package suncertify.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import suncertify.EveryTest;
import suncertify.domain.SubContractor;
import suncertify.domain.SubContractorHandler;

public class SubContractorServiceLocalTest {
	
	private SubContractorServiceLocal manager;
	private SubContractor testSc;

	@Before
	public void setUpClass() throws Exception {
	}
	
	@Before
	public void setUp() throws Exception {
		EveryTest.writeStdContent();
		manager = new SubContractorServiceLocal(EveryTest.getDBFile());
		testSc = new SubContractorHandler().createSubContractor(0,new String[]{
				"Bitter Homes & Gardens", 
				"Smallville", 
				"Drywall, Painting, Carpets", 
				"10", 
				"$75.00", 
				""});
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	/*
	 * Ist scheiße umgesetzt. Aber nur für die Testklasse werden equals und Hashcode nicht implementiert!
	 * equals() und hashcode()k sind wieder drin!
	 *
	private boolean contains(List<SubContractor> scList, SubContractor sc) {
		for (SubContractor scTemp : scList) {
			if (	(scTemp.getName()			!= null && scTemp.getName()		    .equals(sc.getName())		) || (scTemp.getName()		 == null && sc.getName()		 == null) &&
					(scTemp.getLocation()		!= null && scTemp.getLocation()	    .equals(sc.getLocation())	) || (scTemp.getLocation()	 == null && sc.getLocation()	 == null) &&
					(scTemp.getSpecialties()	!= null && scTemp.getSpecialties()	.equals(sc.getSpecialties())) || (scTemp.getSpecialties()== null && sc.getSpecialties()	 == null) &&
					(scTemp.getSize()			!= null && scTemp.getSize()		    .equals(sc.getSize())		) || (scTemp.getSize()		 == null && sc.getSize()		 == null) &&
					(scTemp.getRate()			!= null && scTemp.getRate()		    .equals(sc.getRate())		) || (scTemp.getRate()		 == null && sc.getRate()		 == null) &&
					(scTemp.getCustomer()			!= null && scTemp.getCustomer()		.equals(sc.getCustomer())		) || (scTemp.getCustomer()		 == null && sc.getCustomer()		 == null)
					) {
				return true;
			}
		}
		return false;
	}
	*/

	@Test
	public void testSearchAnd() {
		List<SubContractor> list = null;
		try {
			SearchCriteria searchCrit = null;
			
			searchCrit = new SearchCriteria();
			searchCrit.setName("Bitter Homes & Gardens");
			searchCrit.setLocation("Smallville");
			searchCrit.setSearchAnd(true);
			list = manager.search(searchCrit);
			assertEquals(1, list.size());
			assertTrue(list.contains(testSc));
			
			searchCrit = new SearchCriteria();
			searchCrit.setName("Bitter Homes & Gardens");
			searchCrit.setLocation("Whoville");
			searchCrit.setSearchAnd(true);
			list = manager.search(searchCrit);
			assertEquals(0, list.size());
			
			searchCrit = new SearchCriteria();
			searchCrit.setSearchAnd(true);
			list = manager.search(searchCrit);
			assertTrue(list.contains(testSc));
			assertEquals(33, list.size());
		} catch (Exception e) {
			fail("Exception in testSearchAnd");
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSearchOr() {
		List<SubContractor> list = null;
		
		try {
			SearchCriteria searchCrit = null;
			
			searchCrit = new SearchCriteria();
			searchCrit.setName("Bitter Homes & Gardens");
			searchCrit.setLocation("Smallville");
			searchCrit.setSearchAnd(false);			
			list = manager.search(searchCrit);
			assertEquals(8, list.size());
			assertTrue(list.contains(testSc));
			
			searchCrit = new SearchCriteria();
			searchCrit.setName("Bitter Homes & Gardens");
			searchCrit.setSearchAnd(false);	
			list = manager.search(searchCrit);
			assertEquals(6, list.size());
			assertTrue(list.contains(testSc));
			
			searchCrit = new SearchCriteria();
			searchCrit.setSearchAnd(false);	
			list = manager.search(searchCrit);
			assertEquals(33, list.size());
			assertTrue(list.contains(testSc));
		} catch (Exception e) {
			fail("Exception in testSearchOr");
			e.printStackTrace();
		}
			
	}
	
}
