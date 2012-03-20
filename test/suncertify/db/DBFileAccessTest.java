package suncertify.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import suncertify.EveryTest;

public class DBFileAccessTest {

    private static File dbFile = EveryTest.getDBFile();
    private static final int stdRecCount = 33;
    private DBFileAccess dbFileAccess;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        EveryTest.writeStdContent();
        dbFileAccess = new DBFileAccess(dbFile);
    }

    @After
    public void tearDown() throws Exception {
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testPrivateFieldsAndHeader() {
        Class<DBFileAccess> clazz = DBFileAccess.class;
        try {
            Field hl = clazz.getDeclaredField("dbColumns");
            hl.setAccessible(true); // !!!
            assertEquals(((List) hl.get(dbFileAccess)).size(), 6);
        } catch (NoSuchFieldException nsfe) {
            nsfe.printStackTrace();
            fail("An exception occurred.");
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
            fail("An exception occurred.");
        }

    }

    @Test
    public void testRead() {
        try {
            String[] rec0 = dbFileAccess.read(0);
            assertEquals(6, rec0.length);
            assertEquals("Bitter Homes & Gardens", rec0[0]);

            String[] rec2 = dbFileAccess.read(2);
            assertEquals(6, rec2.length);
            assertEquals("Fred & Nobby", rec2[0]);
            assertEquals("Smallville", rec2[1]);
            assertEquals("Drywall", rec2[2]);
            assertEquals("9", rec2[3]);
            assertEquals("$85.00", rec2[4]);
            assertEquals("", rec2[5]);

            String[] rec32 = dbFileAccess.read(32);
            assertEquals(6, rec32.length);
            assertEquals("Bitter Homes & Gardens", rec32[0]);
            assertEquals("Lendmarch", rec32[1]);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An exception occurred.");
        }
    }

    @Test(expected = RecordNotFoundException.class)
    public void testReadDeleted() throws Exception {
        dbFileAccess.delete(2);
        dbFileAccess.read(2);
    }

    @Test(expected = RecordNotFoundException.class)
    public void testReadException() throws RecordNotFoundException, IOException {
        dbFileAccess.read(stdRecCount); // stdRecCount Datensätze verfügbar, bei
        // 0 beginnend
    }

    @Test
    public void testGetAllValidRecords() {
        Class<DBFileAccess> clazz = DBFileAccess.class;
        try {
            Method gavr = clazz.getDeclaredMethod("getAllValidRecords",
                    new Class<?>[] {});
            gavr.setAccessible(true); // !!!
            assertEquals(stdRecCount, ((ArrayList<?>) gavr.invoke(dbFileAccess,
                    new Object[] {})).size());
        } catch (Exception e) {
            e.printStackTrace();
            fail("An exception occurred.");
        }
    }

    @Test
    public void testGetAllDeletedRecords() {
        Class<DBFileAccess> clazz = DBFileAccess.class;
        try {
            Method gavr = clazz.getDeclaredMethod("getAllDeletedRecords",
                    new Class<?>[] {});
            gavr.setAccessible(true); // !!!
            assertEquals(0, ((ArrayList<?>) gavr.invoke(dbFileAccess,
                    new Object[] {})).size());

            dbFileAccess.delete(2);
            assertEquals(1, ((ArrayList<?>) gavr.invoke(dbFileAccess,
                    new Object[] {})).size());

            dbFileAccess.delete(3);
            dbFileAccess.delete(18);
            assertEquals(3, ((ArrayList<?>) gavr.invoke(dbFileAccess,
                    new Object[] {})).size());

        } catch (Exception e) {
            e.printStackTrace();
            fail("An exception occurred.");
        }
    }

    @Test
    public void testFind() {
        try {
            assertEquals(
                    stdRecCount,
                    dbFileAccess.find(new String[] { "", "", "", "", "", "" }).length);
            assertEquals(
                    stdRecCount,
                    dbFileAccess.find(new String[] { null, null, null, null,
                            null, null }).length);
            assertEquals(
                    6,
                    dbFileAccess.find(new String[] { "Bitter Homes & Gardens",
                            "", "", "", "", "" }).length);
            assertEquals(
                    6,
                    dbFileAccess.find(new String[] { "Bitter Homes & Gardens",
                            "", "", "", "", null }).length);
            assertEquals(
                    1,
                    dbFileAccess.find(new String[] { "Bitter Homes & Gardens",
                            "", "Plumbing", "", "", "" }).length);
            assertEquals(
                    2,
                    dbFileAccess.find(new String[] { "", "Hobbito", "Heat", "",
                            "", "" }).length);
            assertEquals(
                    0,
                    dbFileAccess.find(new String[] { "Bitter Homes & Gardens",
                            "Smallville", "Drywall, Painting, Carpets", "10",
                            "$75.00", "" })[0]);
            assertEquals(
                    2,
                    dbFileAccess.find(new String[] { "Fred & Nobby",
                            "Smallville", "Drywall", "9", "$85.00", "" })[0]);
            assertEquals(
                    10,
                    dbFileAccess.find(new String[] { "", "", "Heating", "",
                            "$", "" }).length);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An exception occurred.");
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindException() throws RecordNotFoundException, IOException {
        dbFileAccess.find(new String[] { "", "", "", "", "" }); // Müssten
        // eigentlich 6
        // sein
    }

    @Test
    public void testUpdate() {
        try {
            int updateRecNo = 10;
            int updateField = 0;
            String updateStr = "New name";
            String[] recData = null;

            recData = dbFileAccess.read(updateRecNo);
            assertFalse(recData[updateField].equals(updateStr));
            recData[updateField] = updateStr;
            dbFileAccess.update(updateRecNo, recData);
            recData = dbFileAccess.read(updateRecNo);
            assertEquals(recData[updateField], updateStr);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An exception occurred.");
        }
    }

    @Test(expected = RecordNotFoundException.class)
    public void testUpdateRecordNotFoundException()
            throws RecordNotFoundException, IOException {
        dbFileAccess.delete(4);
        dbFileAccess.update(4, new String[] { "", "", "", "", "", "" });
    }

    @Test
    public void testDelete() {
        try {
            Class<DBFileAccess> clazz = DBFileAccess.class;
            Method gavr = clazz.getDeclaredMethod("getAllDeletedRecords",
                    new Class<?>[] {});
            gavr.setAccessible(true); // !!!

            int diff = ((ArrayList<?>) gavr.invoke(dbFileAccess,
                    new Object[] {})).size();
            dbFileAccess.delete(10);
            dbFileAccess.delete(11);
            dbFileAccess.delete(20);
            diff = ((ArrayList<?>) gavr.invoke(dbFileAccess, new Object[] {}))
                    .size() - diff;
            assertEquals(3, diff);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An exception occurred.");
        }
    }

    @Test
    public void testGetFirstInsertRecNo() {
        try {
            Class<DBFileAccess> clazz = DBFileAccess.class;
            Method gavr = clazz.getDeclaredMethod(
                    "getFirstPossibleInsertRecNo", new Class<?>[] {});
            gavr.setAccessible(true); // !!!

            int recNoNew = -1;
            recNoNew = (Integer) gavr.invoke(dbFileAccess, new Object[] {});
            assertEquals(stdRecCount, recNoNew);

            int recNoDel = 4;
            dbFileAccess.delete(recNoDel);
            recNoNew = (Integer) gavr.invoke(dbFileAccess, new Object[] {});
            assertEquals(recNoDel, recNoNew);

        } catch (Exception e) {
            e.printStackTrace();
            fail("An exception occurred.");
        }
    }

    @Test
    public void testCreate() {
        try {
            int deleteRecNo = 5;
            String newName1 = "New name";
            String newName2 = "Another new name";

            dbFileAccess.delete(deleteRecNo);
            dbFileAccess.create(new String[] { newName1, "New location",
                    "New spec", "4", "$44", "" });
            assertEquals(newName1, dbFileAccess.read(deleteRecNo)[0]);

            String[] newSC = new String[] { newName2, "New location",
                    "New spec", "4", "$44", "" };
            dbFileAccess.create(newSC);
            int newPos = (stdRecCount - 1) + 1; // Fängt bei 0 an / muss eins
            // hochgezählt werden
            assertEquals(newName2, dbFileAccess.read(newPos)[0]);

        } catch (Exception e) {
            e.printStackTrace();
            fail("An exception occurred: " + e.getClass().getCanonicalName()
                    + " - " + e.getMessage());
        }
    }

}
