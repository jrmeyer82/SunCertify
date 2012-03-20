package suncertify;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import suncertify.db.ConcurrentAccessTest;
import suncertify.db.DBFileAccessTest;
import suncertify.db.DataTest;
import suncertify.domain.SubContractorTest;
import suncertify.service.SubContractorServiceLocalTest;
import suncertify.util.PropertyManagerTest;

@RunWith(Suite.class)
@SuiteClasses({
	DBFileAccessTest.class,
	DataTest.class,
	SubContractorTest.class,
	SubContractorServiceLocalTest.class,
	PropertyManagerTest.class,
	ConcurrentAccessTest.class // Zum Schluss, da ja hier weitere Threads gestartet werden, die die DB-Datei manipulieren und so das Ergebnis verfälschen!
})
public class EveryTest {

	private static File dbFile = new File("files/db-files/workcopy.db");
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		EveryTest.writeStdContent();		
	}
	
	@Before
	public void setUp() throws Exception {
	    
	}
	
	@After
	public void tearDown() throws Exception {
	    
	}
	
	@AfterClass
	public static void tearDownClass() throws Exception {
		writeStdContent();
	}
	
	public static void writeStdContent() {
		try {
			File stdFile = new File("files/db-files/stdContent.db");
			FileInputStream fis = new FileInputStream(stdFile);
			byte[] stdContent = new byte[(int) stdFile.length()];  
			fis.read(stdContent);
			
			dbFile.delete();
			FileOutputStream fos = new FileOutputStream(dbFile, false);
			fos.write(stdContent);
		} catch (Exception e) {
			System.err.println("Could not write standard file content");
		}
	}
	
	public static File getDBFile() {
		return dbFile;
	}

}
