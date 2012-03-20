package suncertify.db;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation class of the DBMain interface. <br>
 * The <code>Data</code> class is the <code>public</code> known access class of
 * the database layer. It manages a thread-safe communication to a file based
 * database and provides a locking mechanism.
 * 
 * @author Jochen R. Meyer
 * 
 */
public class Data implements DBMain {

	/** Standard error message for database access errors */
	private static final String ERROR_MESSAGE_DB_ACCESS = "Database file "
			+ "access error while accessing record '%s'";

	/** Standard error message if a modification on a locked record is not done
	 * by the real lock owner */
	private static final String ERROR_MESSAGE_NOT_LOCK_OWNER = "The Thread "
			+ "trying to modify record '%d' does not own the lock "
			+ "of this record";

	/** Reference to the underlying class actually performing real file access */
	private DBFileAccess database;

	/** A map containing all currently locked recNo's and their lock owners */
	private Map<Integer, Long> locks;

	/**
	 * Constructs the <code>Data</code> class and takes a <code>File</code>
	 * object of the database file to use. If the given <code>File</code> object
	 * is not a valid database file an <code>IOException</code> is thrown.
	 * 
	 * @param dbFile
	 *            the database file to use
	 * @throws IOException
	 *             if the given file cannot be accessed or is no valid database
	 *             file
	 */
	public Data(File dbFile) throws IOException {
		database = new DBFileAccess(dbFile);
		locks = new HashMap<Integer, Long>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int create(String[] data) throws DuplicateKeyException {
		synchronized (database) {
			try {
				return database.create(data);
			} catch (IOException ioe) {
				throw new DuplicateKeyException(ERROR_MESSAGE_DB_ACCESS, ioe);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(int recNo) throws RecordNotFoundException {
		synchronized (database) {
			try {
				if (isCurrentThreadOwnsLock(recNo)) {
					database.delete(recNo);
				} else {
					throw new RecordNotFoundException(
							formatNotLockOwnerMessage(recNo));
				}
			} catch (IOException ioe) {
				throw new RecordNotFoundException(formatDBAccessMessage(recNo),
						ioe);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int[] find(String[] criteria) throws RecordNotFoundException {
		synchronized (database) {
			try {
				return database.find(criteria);
			} catch (IOException ioe) {
				throw new RecordNotFoundException(formatDBAccessMessage(null),
						ioe);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLocked(int recNo) throws RecordNotFoundException {
		synchronized (database) {
			return isRecordLocked(recNo);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void lock(int recNo) throws RecordNotFoundException {
		synchronized (database) {
			
			/*
			 * Wait until a present lock is released. Also wait if the same
			 * Thread is trying to acquire a lock on the same record id twice by
			 * mistake.
			 */
			while (isLocked(recNo) && !isCurrentThreadOwnsLock(recNo)) {
				try {
					database.wait();
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
					return;
				}
			}
			try {
				
				// lock the record if it is still a valid one
				if (database.isValidRecord(recNo)) {
					lockRecord(recNo);
				} else {
					throw new RecordNotFoundException(
							"Unable to lock not existing record '" + recNo
									+ "'");
				}
			} catch (IOException io) {
				throw new RecordNotFoundException(io.getMessage(), io);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] read(int recNo) throws RecordNotFoundException {
		synchronized (database) {
			try {
				return database.read(recNo);
			} catch (IOException ioe) {
				throw new RecordNotFoundException(formatDBAccessMessage(recNo),
						ioe);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void unlock(int recNo) throws RecordNotFoundException {
		synchronized (database) {
			
			/*
			 * The lock must be released in any case - no matter if the record
			 * is valid or not.
			 */
			try {
				if (isLocked(recNo) && isCurrentThreadOwnsLock(recNo)) {
					unlockRecord(recNo);
				}
				
			/*
			 * Finally needed to be sure all waiting Threads are notified
			 * even if an Exception occurs.
			 */
			} finally {
				database.notifyAll();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(int recNo, String[] data) throws RecordNotFoundException {
		synchronized (database) {
			try {
				if (isCurrentThreadOwnsLock(recNo)) {
					database.update(recNo, data);
				} else {
					throw new RecordNotFoundException(
							formatNotLockOwnerMessage(recNo));
				}
			} catch (IOException ioe) {
				throw new RecordNotFoundException(formatDBAccessMessage(recNo),
						ioe);
			}
		}
	}

	/**
	 * Formats a database access error message for the given record.
	 * 
	 * @param recNo
	 *            the record to use in the message
	 * @return the formated message
	 */
	private String formatDBAccessMessage(Integer recNo) {
		return String.format(ERROR_MESSAGE_DB_ACCESS,
				recNo == null ? "[unknown]" : recNo);
	}

	/**
	 * Formats a error message indicating that the accessing thread does not own
	 * the lock of the given record.
	 * 
	 * @param recNo
	 *            the record to use in the message
	 * @return the formated message
	 */
	private String formatNotLockOwnerMessage(int recNo) {
		return String.format(ERROR_MESSAGE_NOT_LOCK_OWNER, recNo);
	}

	/**
	 * Checks if the current thread does own the lock of the given record.
	 * 
	 * @param recNo
	 *            the number of the record to check
	 * @return <code>true</code> if the current thread owns the lock,
	 *         <code>false</code> otherwise
	 */
	private boolean isCurrentThreadOwnsLock(int recNo) {
		Long currentThreadId = Thread.currentThread().getId();
		return currentThreadId.equals(locks.get(recNo));
	}

	/**
	 * Checks if the given record is locked.
	 * 
	 * @param recNo
	 *            the number of the record to check
	 * @return <code>true</code> if the record is locked, <code>false</code>
	 *         otherwise
	 */
	private boolean isRecordLocked(int recNo) {
		return locks.containsKey(recNo);
	}

	/**
	 * Locks a record.
	 * 
	 * @param recNo
	 *            the number of the record to lock
	 */
	private void lockRecord(int recNo) {
		locks.put(recNo, Thread.currentThread().getId());
	}

	/**
	 * Releases the lock from a record.
	 * 
	 * @param recNo
	 *            the number of the record to unlock
	 */
	private void unlockRecord(int recNo) {
		locks.remove(recNo);
	}

}
