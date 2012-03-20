package suncertify.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

public class DBFileCreator {

    private static File dbFile = new File("files/db-files/workcopyHuge.db");

    private static String[] getGenericData(int nr, int booker) {
	String bookerStr = "" + booker;
	String[] input = new String[6];
	input[0] = "Test Contractor " + nr;
	input[1] = "Trier";
	input[2] = "What you need";
	input[3] = "4";
	input[4] = "$44";
	input[5] = bookerStr.length() > 8 ? bookerStr.substring(0, 8)
		: bookerStr;
	return input;
    }

    public static void main(String[] args) {
	writeGenericFile(100000);
	DbFilePrinter.printFile(dbFile, false);
    }

    public static void writeGenericFile(int additionalEntries) {
	try {
	    File stdFile = new File("files/db-files/stdContent.db");
	    FileInputStream fis = new FileInputStream(stdFile);
	    byte[] stdContent = new byte[(int) stdFile.length()];
	    fis.read(stdContent);
	    if (dbFile.exists()) {
		dbFile.delete();
	    }
	    FileOutputStream fos = new FileOutputStream(dbFile, false);
	    fos.write(stdContent);
	    fos.close();
	    long startTime = System.currentTimeMillis();
	    for (int i = 1; i <= additionalEntries; i++) {
		create(i + 32, getGenericData(i, ((int) (Math.random() * 10))));
		if (i % 50 == 0) {
		    long time = System.currentTimeMillis() - startTime;
		    System.out.println(String.format(
			    "Eintrag %5d erstellt (%2dh %2dm %2ds)", i,
			    (time % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000),
			    (time % (60 * 60 * 1000)) / (60 * 1000),
			    (time % (60 * 1000)) / 1000));
		}
	    }
	} catch (Exception e) {
	    System.err.println("Could not write standard file content");
	}
    }

    private static void create(int recNo, String[] data) {
	int dataSectionStart = 54;
	int dataSetLength = 183;
	try {
	    RandomAccessFile raf = new RandomAccessFile(dbFile, "rw");
	    int writePos = dataSectionStart + (dataSetLength * recNo);
	    raf.seek(writePos);
	    byte[] buf = { 00 };
	    raf.write(buf);
	    writePos += 1;
	    int[] colLengths = new int[] { 32, 64, 64, 6, 8, 8 };
	    for (int i = 0; i < colLengths.length; i++) {
		String str = data[i];
		raf.seek(writePos);
		// data is padded right (i.e.: left-justified)
		str = String.format("%1$-" + colLengths[i] + "s",
			(str != null ? str : ""));
		buf = str.getBytes();
		raf.write(buf);
		writePos += colLengths[i];
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

}
