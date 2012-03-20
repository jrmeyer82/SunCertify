package suncertify.service;

import java.io.File;
import java.util.List;

import suncertify.domain.SubContractor;

/**
 * This class is the standalone <code>SubContractorService</code> implementation
 * of the system. All method calls are passed to its superclass
 * <code>Worker</code> which implements the functionality.
 * 
 * @author Jochen R. Meyer
 * 
 */
public class SubContractorServiceLocal extends Worker implements
		SubContractorService {

	/**
	 * Constructs the object using the given file as database file.
	 * 
	 * @param dbFile
	 *            the database file to connect to
	 */
	SubContractorServiceLocal(File dbFile) {
		super(dbFile);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void book(SubContractor subContractor, String customer)
			throws SubContractorNotFoundException,
			SubContractorAlreadyBookedException {
		super.book(subContractor, customer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearBooking(SubContractor subContractor)
			throws SubContractorNotFoundException,
			SubContractorAlreadyBookedException {
		super.clearBooking(subContractor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getConnectionAsString() {
		return "Connected to database file: " + dbFile.getAbsolutePath();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SubContractor getSubContractorByRecNo(int recNo)
			throws SubContractorNotFoundException {
		return super.getSubContractorByRecNo(recNo);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SubContractor> search(SearchCriteria searchCriteria)
			throws SubContractorNotFoundException {
		return super.search(searchCriteria);
	}
}
