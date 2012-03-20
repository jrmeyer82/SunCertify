package suncertify.db;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;

public class DbFilePrinter {

	public static void main(String[] args) {
		printFile(new File("files/db-files/workcopy.db"), true);
	}

	/**
	 * A. Start of file 4 byte numeric, magic cookie value. Identifies this as a
	 * data file 2 byte numeric, number of fields in each record
	 * 
	 * B. Schema description section. Repeated for each field in a record: 1
	 * byte numeric, length in bytes of field name n bytes (defined by previous
	 * entry), field name 1 byte numeric, field length in bytes end of repeating
	 * block
	 * 
	 * C. Data section. Repeat to end of file: 1 byte flag. 00 implies valid
	 * record, 0xFF implies deleted record Record containing fields in order
	 * specified in schema section, no separators between fields, each field
	 * fixed length at maximum specified in schema information
	 */
	public static void printFile(File file, boolean printByteData) {

		try {
			/* Byte data nicht ausgeben
			BufferedReader brComplete = null;
			brComplete = new BufferedReader(new FileReader(file));
			String line = "";
			while (printByteData && (line = brComplete.readLine()) != null) {
				System.out.println(line);
			}*/			
			System.out.println("**************************************");

			// Datei-Informationen
			FileInputStream fis = new FileInputStream(file);

			byte[] magicCookieValue = new byte[4];
			fis.read(magicCookieValue);
			System.out.print("magicCookieValue: "
					+ new BigInteger(magicCookieValue) + " --- ");
			for (byte b : magicCookieValue) {
				System.out.print(b + "|");
			}
			int fieldCount = 0;
			byte[] fieldCountArray = new byte[2];
			fis.read(fieldCountArray);
			fieldCount = new BigInteger(fieldCountArray).intValue();
			System.out.print("\nfieldCount: " + fieldCount + " --- ");
			for (byte b : fieldCountArray) {
				System.out.print(b + "|");
			}
			System.out.println("\n**************************************");

			// Feld-Informationen
			int dataSectionStart = 6;
			int dataSetLength = 1;
			String[] header = new String[fieldCount];
			int[] lenghts = new int[fieldCount];
			for (int i = 0; i < fieldCount; i++) {

				byte[] fieldNameLengthArray = new byte[1];
				fis.read(fieldNameLengthArray);
				int fieldNameLengthInBytes = new BigInteger(
						fieldNameLengthArray).intValue();

				byte[] fieldNameArray = new byte[fieldNameLengthInBytes];
				fis.read(fieldNameArray);
				String fieldName = new String(fieldNameArray);

				byte[] fieldLenghtArray = new byte[1];
				fis.read(fieldLenghtArray);
				int fieldLengthInBytes = new BigInteger(fieldLenghtArray)
						.intValue();

				System.out.println("Field name length: "
						+ fieldNameLengthInBytes);
				System.out.println("Field name: " + fieldName);
				System.out.println("Field length: " + fieldLengthInBytes);
				System.out.println();

				header[i] = fieldName;
				lenghts[i] = fieldLengthInBytes;

				dataSectionStart += 1; // behind field name length byte
				dataSectionStart += fieldNameLengthInBytes; // behind field name
															// bytes
				dataSectionStart += 1; // behind field length byte
				dataSetLength += fieldLengthInBytes;
			}
			System.out.println("**************************************");

			System.out.println("DataSectionStart: " + dataSectionStart);
			System.out.println("DataSetLength: " + dataSetLength);

			System.out.println("");
			System.out.println("**************************************");
			System.out.println("");

			// Daten
			System.out.print(String.format("%1$-" + 8 + "s", "flag"));
			for (int i = 0; i < fieldCount; i++) {
				System.out.print(String.format("%1$-" + lenghts[i] + "s",
						header[i]));
			}
			System.out.println();
			byte[] cell = null;
			while (fis.available() > 0) {
				cell = new byte[1]; // Flag
				fis.read(cell);
				System.out.print(String.format("%1$-" + 8 + "s",
						new BigInteger(cell).intValue()));
				for (int i = 0; i < fieldCount; i++) {
					cell = new byte[lenghts[i]];
					fis.read(cell);
					String cellValue = new String(cell);
					System.out.print(String.format("%1$-" + lenghts[i] + "s",
							cellValue));
				}
				System.out.println();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
