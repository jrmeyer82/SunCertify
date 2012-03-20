package suncertify.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import suncertify.EveryTest;

/**
 * 
 * @author Hans
 */
public class DataTest {

	private static DBMain data;
	private static String[] correctData;

	public DataTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		data = new Data(EveryTest.getDBFile());
		correctData = createCorrectData();
	}

	@Before
	public void setUp() throws Exception {
		EveryTest.writeStdContent();
	}

	@After
	public void tearDown() throws Exception {
	}

	private static String[] createCorrectData() {
		String[] input = new String[6];
		input[0] = "New Test Contractor";
		input[1] = "Trier";
		input[2] = "What you need";
		input[3] = "4";
		input[4] = "$44";
		input[5] = "2";
		return input;
	}

	private static String[] createTooLongData() {
		String[] input = new String[6];
		input[0] = "Test Contractor with a name that should by definition be too long";
		input[1] = "Trier";
		input[2] = "What you need";
		input[3] = "4";
		input[4] = "$44";
		input[5] = "2";
		return input;
	}

	@Test
	public void testCreate() throws Exception {
		int recNo = 0;
		recNo = data.create(correctData);

		System.out.println("Record with number " + recNo + " created");

		String[] readData = data.read(recNo);
		System.out.println(Arrays.toString(readData));
		Assert.assertTrue(Arrays.equals(readData, correctData));

		data.lock(recNo);
		data.delete(recNo);
		data.unlock(recNo);
	}

	@Test
	public void testDelete() {
		int recNo = 4;
		String[] emptyCriteria = new String[]{null, null, null, null, null, null};
		try {
			int elentsBefore = data.find(emptyCriteria).length;
			
			data.lock(recNo);
			data.delete(recNo);
			data.unlock(recNo);
			
			int elentsAfter = data.find(emptyCriteria).length;
			assertEquals(1, elentsBefore - elentsAfter);			
			
			recNo += 2;
			data.lock(recNo);
			data.delete(recNo);
			data.unlock(recNo);
			
			elentsAfter = data.find(emptyCriteria).length;
			assertEquals(2, elentsBefore - elentsAfter);
		} catch (Exception e) {
			fail("Un unexpected exception (" + e.getClass().getCanonicalName() + ") occurred:" + e.getMessage());
			e.printStackTrace();
		}
	}

	@Test(expected = RecordNotFoundException.class)
	public void testRecordNotAvailableAfterDelete() throws Exception {
		int recNo = 0;
		assertTrue(data.read(recNo)[2].equals("Drywall, Painting, Carpets"));
		
		data.lock(recNo);
		data.delete(recNo);
		data.unlock(recNo);
		
		/*data.lock(recNo);
		data.delete(recNo);
		data.unlock(recNo);*/
		data.read(recNo);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindWithTooShortCriteria() throws Exception {
		data.find(new String[0]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindWithNullCriteria() throws Exception {
		data.find(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindWithToLongCriteria() throws Exception {
		data.find(new String[10]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUpdateWithToLongValues() throws Exception {
		int recNo = 4;
		data.lock(recNo);
		data.update(recNo, createTooLongData());
		data.unlock(recNo);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateWithToLongValues() throws Exception {
		data.create(createTooLongData());
	}

	@Test
	public void testFindWithCriteria() throws Exception {
		String[] criteria = new String[6];
		criteria[2] = "Heat";
		int[] result = data.find(criteria);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.length > 0);
	}

}