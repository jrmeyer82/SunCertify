package suncertify.db;

/**
 * The database interface given in the certification assignment. <br>
 * It defines all relevant basic methods for the database access (create,
 * update, read, delete) plus a basic locking mechanism
 * 
 * @author Jochen R. Meyer
 * 
 */
public interface DBMain {

	/**
	 * Creates a new record in the database (possibly reusing a deleted entry).
	 * Inserts the given data, and returns the record number of the new record.
	 * 
	 * @param data
	 *            an array containing the data for the new record in the order
	 *            of the database columns
	 * @return the record number of the created record
	 * @throws DuplicateKeyException
	 *             if a record with the given criteria is already existing
	 */
	public int create(String[] data) throws DuplicateKeyException;

	/**
	 * Deletes a record, making the record number and associated disk storage
	 * available for reuse.
	 * 
	 * @param recNo
	 *            the record number to delete
	 * @throws RecordNotFoundException
	 *             if the given record number could not be found on the database
	 */
	public void delete(int recNo) throws RecordNotFoundException;

	/**
	 * Returns an array of record numbers that match the specified criteria.
	 * Field n in the database file is described by criteria[n]. A null value in
	 * criteria[n] matches any field value. A non-null value in criteria[n]
	 * matches any field value that begins with criteria[n]. (For example,
	 * "Fred" matches "Fred" or "Freddy".)
	 * 
	 * @param criteria
	 *            an array containing the values to search for in the order of
	 *            the database columns
	 * @return an array containing the record numbers of the found records
	 * @throws RecordNotFoundException
	 *             if a record could not be read during the search on the
	 *             database
	 */
	public int[] find(String[] criteria) throws RecordNotFoundException;

	/**
	 * Determines if a record is currenly locked. Returns true if the record is
	 * locked, false otherwise.
	 * 
	 * @param recNo
	 *            the record number to check for an active lock on it
	 * @return <code>true</code> if the record is locked, <code>false</code>
	 *         otherwise
	 * @throws RecordNotFoundException
	 *             if the given record number could not be found on the database
	 */
	public boolean isLocked(int recNo) throws RecordNotFoundException;

	/**
	 * Locks a record so that it can only be updated or deleted by this client.
	 * If the specified record is already locked, the current thread gives up
	 * the CPU and consumes no CPU cycles until the record is unlocked.
	 * 
	 * @param recNo
	 *            the record number to lock
	 * @throws RecordNotFoundException
	 *             if the given record number could not be found on the database
	 */
	public void lock(int recNo) throws RecordNotFoundException;

	/**
	 * Reads a record from the file. Returns an array where each element is a
	 * record value.
	 * 
	 * @param recNo
	 *            the record number to read
	 * @return an array representing the read data in the order of the database
	 *         columns
	 * @throws RecordNotFoundException
	 *             if the given record number could not be found on the database
	 */
	public String[] read(int recNo) throws RecordNotFoundException;

	/**
	 * Releases the lock on a record.
	 * 
	 * @param recNo
	 *            the record number to unlock
	 * @throws RecordNotFoundException
	 *             if the given record number could not be found on the database
	 */
	public void unlock(int recNo) throws RecordNotFoundException;

	/**
	 * Modifies the fields of a record. The new value for field n appears in
	 * data[n].
	 * 
	 * @param recNo
	 *            the record number to update
	 * @param data
	 *            the new data of the record
	 * @throws RecordNotFoundException
	 *             if the given record number could not be found on the database
	 */
	public void update(int recNo, String[] data) throws RecordNotFoundException;
}