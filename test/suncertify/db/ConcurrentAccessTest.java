package suncertify.db;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import suncertify.EveryTest;

/**
 * 
 * @author Hans
 */
public class ConcurrentAccessTest {

	private static DBMain data;
	private static String[] correctDataPara;
	private static String[] correctDataHeavy;
	@SuppressWarnings("unused")
    private static String[] brokenData;
	@SuppressWarnings("unused")
    private static String[] tooLongData;

	public ConcurrentAccessTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		data = new Data(EveryTest.getDBFile());
		correctDataPara = createCorrectData();
		correctDataPara[0] = correctDataPara[0] + "Para";
		correctDataHeavy = createCorrectData();
		correctDataHeavy[0] = correctDataHeavy[0] + "Heavy";
		brokenData = createDataWithWrongDataTypes();
		tooLongData = createTooLongData();
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
		input[0] = "Test Contractor";
		input[1] = "Trier";
		input[2] = "What you need";
		input[3] = "4";
		input[4] = "$44";
		input[5] = "12345678";
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

	private static String[] createDataWithWrongDataTypes() {
		String[] input = new String[6];
		input[0] = "Broken Data Test User";
		input[1] = "Trier";
		input[2] = "What you need";
		input[3] = "blubb";
		input[4] = "bla";
		input[5] = "2";
		return input;
	}

	@Test
	public void testCreateInParallel() {
		System.out.println("<Running parallel test>");
		Runnable r = new Runnable() {

			public void run() {
				try {
					System.out.println("Thread started");
					Thread.sleep(((int) (Math.random() * 10)) * 1000);
					System.out.println("Done with sleeping");
					try {
						int recNo = data.create(correctDataPara);
						System.out.println("Thread "
								+ Thread.currentThread().getId()
								+ " created record number " + recNo);
					} catch (DuplicateKeyException dke) {
						System.out
								.println("Thread "
										+ Thread.currentThread().getId()
										+ " could create record because it already exists");
					}
				} catch (Exception dke) {
					dke.printStackTrace();
				}
			}
		};

		for (int i = 0; i < 200; i++) {
			Thread t = new Thread(r);
			t.start();
		}
		/*try { // Sonst könnten Threads hieraus und aus testHeavyLoad auf den gleichen Datensatz zugreifen.
			System.out.println("Schafen um die Threads zuende machen zu lassen");
			Thread.sleep(10 * 1000);
		} catch (Exception e) {System.out.println("Beim schlafen gestört");}*/
		System.out.println("<Done with parallel test>\n");
	}

	@Test
	public void testHeavyLoad() {
		System.out.println("<Running heavy load test>");

		Runnable updater = new Runnable() {

			public void run() {
				for (int i = 0; i < 20; i++) {
					int id = (int) (Math.random() * 30);
					try {
						String[] dataString = data.read(id);
						Thread.sleep(50);
						dataString[5] = Integer
								.toString((int) (Math.random() * 100));
						data.lock(id);
						data.update(id, dataString);
						System.out.println("Subcontractor " + id + " updated.");
					} catch (Exception e) {
						System.out.println(e.getMessage());
					} finally {
						try {
							data.unlock(id);
						} catch (Exception e) {
							System.out.println(e.getMessage());
						}
					}
				}
			}
		};
		Runnable deleter = new Runnable() {

			public void run() {
				for (int i = 0; i < 20; i++) {
					int id = (int) (Math.random() * 30);
					try {
						data.lock(id);
						data.delete(id);
						System.out.println("Subcontractor " + id + " deleted.");
						Thread.sleep(50);
					} catch (Exception e) {
						System.out.println(e.getMessage());
					} finally {
						try {
							data.unlock(id);
						} catch (Exception e) {
							System.out.println(e.getMessage());
						}
					}
				}
			}
		};

		Runnable creator = new Runnable() {

			public void run() {
				for (int i = 0; i < 20; i++) {
					try {
						correctDataHeavy[0] = "New Test Contractor "
								+ (int) (Math.random() * 500);
						Thread.sleep(50);
						int id = data.create(correctDataHeavy);
						System.out.println("Subcontractor " + id + " created.");
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}
			}
		};

		for (int i = 0; i < 100; i++) {
			System.out.println("Thread-start loop nr. " + i);
			Thread d = new Thread(deleter);
			Thread u = new Thread(updater);
			Thread c = new Thread(creator);
			d.start();
			u.start();
			c.start();
			
			// Auf schnellen PCs gehen sonst zu viele Threads gleichzeitig auf --> java.lang.OutOfMemoryError: unable to create new native thread
			if ((i % 10) == 0) {
				try {
					System.out.println("Pause machen mit neuen Threads");
					Thread.sleep(4 * 1000);
				} catch (Exception e) {
					System.err.println("sleep interrupted!!");
				}
			}
		}

		System.out.println("<Done with heavy load test>\n");
	}

	public static void main(String[] args) throws Exception {
		ConcurrentAccessTest test = new ConcurrentAccessTest();
		ConcurrentAccessTest.setUpClass();
		test.testHeavyLoad();
	}

}
