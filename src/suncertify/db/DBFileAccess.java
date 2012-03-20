package suncertify.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The <code>DBFileAccess</code> class is the part of the database layer which
 * actually executes the read and write operations on the database file. It is
 * not thread safe and serves as a helper class for <code>Data</code>.
 * 
 * @author Jochen R. Meyer
 * 
 */
class DBFileAccess {

	/** Predefined magic cookie value of a valid database file */
	private static final int MAGIC_COOKIE_VALUE = 515;

	/** Predefined number of bytes containing the magic cookie value */
	private static final int MAGIC_COOKIE_VALUE_BYTES = 4;

	/** Predefined value indicating a valid database record */
	private static final byte RECORD_VALID = 00;

	/** Predefined value indicating a database record marked as deleted */
	private static final byte RECORD_DELETED = (byte) 0xFF;

	/** Standard message for a failed database update */
	private static final String MSG_DB_UPDATE_FAIL = "Database update failed.";

	/** The object reaizing the read/write operations */
	private RandomAccessFile raf;

	/** The actual start of the data section (to be read from the database 
	 * file) */
	private int dataSectionStart;

	/** A list of all database columns present in the database file */
	private List<DBColumn> dbColumns;

	/** The length of one dataset in the database file (i.e.: the sum of all
	 * columns' lengths) */
	private int dataSetLength;

	/**
	 * Contructs the object without any arguments.
	 */
	DBFileAccess() {
		dbColumns = new ArrayList<DBColumn>();
	}

	/**
	 * Constructs a <code>DBFileAccess</code> object and initializes it with a
	 * connection to the given database <code>File</code> object.
	 * 
	 * @param dbFile
	 *            the database file to operate on
	 * @throws IOException
	 *             if the database file cannot be accessed properly
	 */
	DBFileAccess(File dbFile) throws IOException {
		this();
		openConnection(dbFile);
	}

	/**
	 * Inserts a new record into the database file. If there are deleted
	 * records, the first available deleted record is overwritten. If not, the
	 * database file is extended and the record is added after the last the
	 * record in the file.
	 * 
	 * @param data
	 *            the data of the new record
	 * @return the record number of the newly created record, i.e.: the
	 *         position, the record has been inserted
	 * @throws DuplicateKeyException
	 *             if a record with the same data is already existing
	 * @throws IOException
	 *             if the database file cannot be accessed properly
	 */
	int create(String[] data) throws DuplicateKeyException, IOException {
		String[] dataCopy = Arrays.copyOf(data, data.length);
		dataCopy[5] = null; // Omit check for customer
		int recNo = getFirstPossibleInsertRecNo();
		try {
			if (find(dataCopy).length > 0) { // The record is already present
				throw new DuplicateKeyException("Record is already present "
						+ "in database");
			}
			checkData(data);
			setPointerToRecord(recNo);
			writeByte(RECORD_VALID);
			update(recNo, data);
		} catch (RecordNotFoundException rnfe) {
			throw new IOException("Could not verify write data integrity");
		}
		return recNo;
	}

	/**
	 * Mark the given record as deleted in the database, i.e.: make it
	 * unavailable for reading.
	 * 
	 * @param recNo
	 *            the number of the record to mark as deleted
	 * @throws RecordNotFoundException
	 *             if the given record is not/no more a valid record
	 * @throws IOException
	 *             if the database file cannot be accessed properly
	 */
	void delete(int recNo) throws RecordNotFoundException, IOException {
		checkValidRecNo(recNo);
		setPointerToRecord(recNo);
		writeByte(RECORD_DELETED);
	}

	/**
	 * See {@link DBMain#find(String[]) DBMain.find(String[])} for detailed
	 * explanation.
	 * 
	 * @param criteria
	 *            an array containing the values to search for in the order of
	 *            the database columns
	 * @return an array containing the record numbers of the found records
	 * @throws RecordNotFoundException
	 *             if a record could not be read during the search on the
	 *             database
	 * @throws IOException
	 *             if the database file cannot be accessed properly
	 */
	int[] find(String[] criteria) throws RecordNotFoundException, IOException {
		if (criteria == null || criteria.length != dbColumns.size()) {
			throw new IllegalArgumentException("The number of criterias must "
					+ "match the number of database fields.");
		}
		List<Integer> found = new ArrayList<Integer>();

		for (Integer recNo : getAllValidRecords()) {
			String[] rec = read(recNo);
			boolean matches = true;
			for (int i = 0; (i < rec.length) && matches; i++) {
				matches = (criteria[i] == null) || (criteria[i].equals(""))
						|| (rec[i].startsWith(criteria[i]));
			}
			if (matches) {
				found.add(recNo);
			}
		}

		int[] result = new int[found.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = found.get(i);
		}
		return result;
	}

	/**
	 * Checks if a record is valid or deleted.
	 * 
	 * @param recNo
	 *            the number of the record to check
	 * @return <code>true</code> if the given record is valid,
	 *         <code>false</code> otherwise
	 * @throws IOException
	 *             if the database file cannot be accessed properly
	 */
	boolean isValidRecord(int recNo) throws IOException {
		return getAllValidRecords().contains(recNo);
	}

	/**
	 * Opens a connection to the given database <code>File</code> object.
	 * 
	 * @param dbFile
	 *            the database file to operate on
	 * @throws IOException
	 *             if the database file cannot be accessed properly
	 */
	final void openConnection(File dbFile) throws IOException {
		if (dbFile == null) {
			throw new IllegalArgumentException("Given File paramter is null");
		}
		if (!dbFile.exists()) {
			throw new IOException("The file '" + dbFile + "' is not existing");
		}
		if (!dbFile.canRead()) {
			throw new IOException("The file '" + dbFile + "' is not readable");
		}

		// Check magic cookie value
		InputStream is = new FileInputStream(dbFile);
		byte[] buf = new byte[MAGIC_COOKIE_VALUE_BYTES];
		is.read(buf);
		int magicCookieValue = (new BigInteger(buf)).intValue();
		if (magicCookieValue != MAGIC_COOKIE_VALUE) {
			throw new IOException("The file '" + dbFile
					+ "' is not a valid database file");
		}

		raf = new RandomAccessFile(dbFile, "rw");

		dataSectionStart = MAGIC_COOKIE_VALUE_BYTES; // After magic cookie value
		raf.seek(MAGIC_COOKIE_VALUE_BYTES);
		int fieldCount = new BigInteger(readBytes(2)).intValue();
		dataSectionStart += 2; // Behind field count field
		dataSetLength = 1; // including flag
		DBColumn dbCol;

		for (int i = 0; i < fieldCount; i++) {
			dbCol = new DBColumn();
			int fieldNameLength = new BigInteger(readBytes(1)).intValue();
			dataSectionStart += 1; // behind field name length byte
			byte[] fieldName = readBytes(fieldNameLength);
			dbCol.setName(new String(fieldName));
			dataSectionStart += fieldNameLength; // behind field name bytes
			int fieldLength = new BigInteger(readBytes(1)).intValue();
			dbCol.setLength(fieldLength);
			dataSectionStart += 1; // behind field length byte
			dataSetLength += fieldLength;
			dbCol.setNumber(i);
			dbColumns.add(dbCol);
		}
	}

	/**
	 * Returns a <code>String</code> array containing the data of the given
	 * record number.
	 * 
	 * @param recNo
	 *            the record number of the record to read
	 * @return the data of the given record number.
	 * @throws RecordNotFoundException
	 *             if the given record is not/no more a valid record
	 * @throws IOException
	 *             if the database file cannot be accessed properly
	 */
	String[] read(int recNo) throws RecordNotFoundException, IOException {
		String[] record = new String[dbColumns.size()];

		setPointerToRecord(recNo);

		if (raf.getFilePointer() == raf.length()) {
			throw new RecordNotFoundException(
					formatRecordNotFoundMessage(recNo));
		}

		byte flag = readBytes(1)[0];
		if (flag == RECORD_VALID) {
			for (DBColumn dbCol : dbColumns) {
				record[dbCol.getNumber()] = new String(
						readBytes(dbCol.getLength())).trim();
			}
		} else if (flag == RECORD_DELETED) {
			throw new RecordNotFoundException(formatRecordErrorMessage(
					"Record '%d' is marked as deleted", recNo));
		} else {
			throw new IllegalStateException("Database file seems corrupt: "
					+ "an unsupported value for the deletion flag was found.");
		}

		return record;
	}

	/**
	 * Updates the given record with the given data.
	 * 
	 * @param recNo
	 *            the number of the record to update
	 * @param data
	 *            the new data for the record
	 * @throws RecordNotFoundException
	 *             if the given record is not/no more a valid record
	 * @throws IOException
	 *             if the database file cannot be accessed properly
	 */
	void update(int recNo, String[] data) throws RecordNotFoundException,
			IOException {
		checkValidRecNo(recNo);
		checkData(data);
		setPointerToRecordData(recNo);
		for (DBColumn dbCol : dbColumns) {
			String str = data[dbCol.getNumber()];

			// data is padded right (i.e.: left-justified)
			str = String.format("%1$-" + dbCol.getLength() + "s",
					(str != null ? str : ""));
			writeBytes(str.getBytes());
		}
	}

	/**
	 * Checks the given data if it can be written to the database without any
	 * contraint violation like max. column value length. If a violation is
	 * found, an {@link IllegalArgumentException} is thrown.
	 * 
	 * @param data
	 *            the data to check
	 */
	private void checkData(String[] data) {
		List<String> invalidColumns = new ArrayList<String>();
		for (DBColumn dbCol : dbColumns) {
			int colNr = dbCol.getNumber();
			if (data[colNr] != null && data[colNr].length() > dbCol.getLength()) {
				invalidColumns.add(dbCol.getName());
			}
		}
		if (invalidColumns.size() > 0) {
			String invalidCols = invalidColumns.get(0);
			for (int i = 1; i < invalidColumns.size(); i++) {
				invalidCols += ", " + invalidColumns.get(i);
			}
			throw new IllegalArgumentException(MSG_DB_UPDATE_FAIL
					+ " Values for columns " + invalidCols + " too long!");
		}
	}

	/**
	 * Perform the check if the given record is a valid one.
	 * 
	 * @param recNo
	 *            The number of the record to check
	 * @throws RecordNotFoundException
	 *             if the given record is not/no more a valid record
	 * @throws IOException
	 *             if the database file cannot be accessed properly
	 */
	private void checkValidRecNo(int recNo) throws RecordNotFoundException,
			IOException {
		if (!isValidRecord(recNo)) {
			throw new RecordNotFoundException(
					formatRecordErrorMessage(MSG_DB_UPDATE_FAIL
							+ " Record '%d' is no more valid", recNo));
		}
	}

	/**
	 * Format the given message taking the given record as an argument.
	 * 
	 * @param message
	 *            the message to format
	 * @param recNo
	 *            the argument to format in the message
	 * @return a formatted message containg the record number
	 */
	private String formatRecordErrorMessage(String message, int recNo) {
		return (String.format(message, recNo));
	}

	/**
	 * Return a formatted error message if a record could not be found
	 * containing the given record number.
	 * 
	 * @param recNo
	 *            the record number which could not be found
	 * @return A formatted 'record not found' error message containing the given
	 *         record number.
	 */
	private String formatRecordNotFoundMessage(int recNo) {
		return formatRecordErrorMessage("Record '%d' does not exist", recNo);
	}

	/**
	 * Convenience method to get all records marked as deleted.
	 * 
	 * @return a <code>List</code> of the numbers of all deleted records
	 * @throws IOException
	 *             if the database file cannot be accessed properly
	 */
	private List<Integer> getAllDeletedRecords() throws IOException {
		return getRecords(false);
	}

	/**
	 * Convenience method to get all valid records.
	 * 
	 * @return a <code>List</code> of the numbers of all valid records
	 * @throws IOException
	 *             if the database file cannot be accessed properly
	 */
	private List<Integer> getAllValidRecords() throws IOException {
		return getRecords(true);
	}

	/**
	 * Determines the first possible insert position for a new record.
	 * 
	 * @return the record number for the record to insert
	 * @throws IOException
	 *             if the database file cannot be accessed properly
	 */
	private int getFirstPossibleInsertRecNo() throws IOException {
		List<Integer> deleted = getAllDeletedRecords();
		if (deleted.size() > 0) {
			return deleted.get(0);
		} else {
			return getAllValidRecords().size();
		}
	}

	/**
	 * Returns a <code>List</code> of record numbers. Depending on the flag all
	 * valid or all deleted records are returned. This method executes the
	 * 'real' read actions for the convenience methods
	 * {@link DBFileAccess#getAllValidRecords() getAllValidRecords} and
	 * {@link DBFileAccess#getAllDeletedRecords() getAllDeletedRecords}.
	 * 
	 * @param valid
	 *            search for valid (<code>true</code>) or deleted (
	 *            <code>false</code>) records
	 * @return depending on the given flag a <code>List</code> of valid or
	 *         deleted record numbers
	 * @throws IOException
	 *             if the database file cannot be accessed properly
	 */
	private List<Integer> getRecords(boolean valid) throws IOException {
		List<Integer> result = new ArrayList<Integer>();
		int recNo = 0;
		int readPos = dataSectionStart;
		byte flag = -1;
		byte flagValue = valid ? RECORD_VALID : RECORD_DELETED;
		while (readPos < raf.length()) {
			raf.seek(readPos);
			flag = readBytes(1)[0];
			if (flag == flagValue) {
				result.add(recNo);
			}
			recNo++;
			readPos = dataSectionStart + (recNo * dataSetLength);
		}
		return result;
	}

	/**
	 * Reads and returns the specified number of bytes from the database file.
	 * 
	 * @param length
	 *            the number of bytes to read
	 * @return a byte array containing the read bytes
	 * @throws IOException
	 *             if the database file cannot be accessed properly or reading
	 *             the specified number of bytes was not possible
	 * 
	 */
	private byte[] readBytes(int length) throws IOException {
		byte[] buf = new byte[length];
		int bytesRead = raf.read(buf);
		if (buf.length != bytesRead) {
			throw new IOException("Could not read correct number of bytes "
					+ "(read: " + bytesRead + ", needed: " + length + ")!");
		} else {
			return buf;
		}
	}

	/**
	 * Makes the <code>RandomAccessFile</code> object point to the specified
	 * record.
	 * 
	 * @param recNo
	 *            the number of the record to which the file pointer shall point
	 * @throws IOException
	 *             if the database file cannot be accessed properly
	 * @throws RecordNotFoundException
	 *             if the given record is not/no more a valid record
	 */
	private void setPointerToRecord(int recNo) throws IOException,
			RecordNotFoundException {
		int position = dataSectionStart + (dataSetLength * recNo);
		raf.seek(position);

		// Throw an exception if the record is not available
		if (position > raf.length()) {
			throw new RecordNotFoundException(
					formatRecordNotFoundMessage(recNo));
		}
	}

	/**
	 * Makes the <code>RandomAccessFile</code> object point to the data of the
	 * specified record, i.e.: behind the status flag.
	 * 
	 * @param recNo
	 *            the number of the record to which data block the file pointer
	 *            shall point
	 * @throws IOException
	 *             if the database file cannot be accessed properly
	 * @throws RecordNotFoundException
	 *             if the given record is not/no more a valid record
	 */
	private void setPointerToRecordData(int recNo) throws IOException,
			RecordNotFoundException {
		setPointerToRecord(recNo);
		long position = raf.getFilePointer();
		raf.skipBytes(1);

		/*
		 * Because the skipBytes() method may fail, we need to check if we
		 * really reached the correct position
		 */
		if (raf.getFilePointer() != (position + 1L)) {
			throw new IOException("Could not seek to desired position");
		}
	}

	/**
	 * Write one <code>byte</code> to the database. Start at the current
	 * position of the <code>RandomAccessFile</code>. Existing data is
	 * overridden.
	 * 
	 * @param b
	 *            the <code>byte</code> to write
	 * @throws IOException
	 *             if the database file cannot be accessed properly
	 */
	private void writeByte(byte b) throws IOException {
		writeBytes(new byte[] { b });
	}

	/**
	 * Write the the content of a <code>byte</code> array to the database. Start
	 * at the current position of the <code>RandomAccessFile</code>. Existing
	 * data is overridden.
	 * 
	 * @param bytes
	 *            the <code>byte</code> array containing the bytes to write
	 * @throws IOException
	 *             if the database file cannot be accessed properly
	 */
	private void writeBytes(byte[] bytes) throws IOException {
		raf.write(bytes);
	}

}
