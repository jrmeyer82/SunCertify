package suncertify.service;

import java.io.File;
import java.rmi.RemoteException;
import java.util.List;

import suncertify.domain.SubContractor;

/**
 * This class is the server side implementation of the client-server system. It
 * uses a <code>Worker</code> instance which implements and provides the
 * functionality.
 * 
 * @author Jochen R. Meyer
 * 
 */
public class SubContractorServerImpl implements SubContractorServer {

	/** The reference to the worker class which implements all the
	 * functionality. */
	private Worker worker;

	/**
	 * Constructs the object using the given file as database file.
	 * 
	 * @param dbFile
	 *            the database file to connect to
	 */
	SubContractorServerImpl(File dbFile) {
		worker = new Worker(dbFile);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void book(SubContractor subContractor, String customer)
			throws RemoteException, SubContractorNotFoundException,
			SubContractorAlreadyBookedException {
		worker.book(subContractor, customer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearBooking(SubContractor subContractor)
			throws RemoteException, SubContractorNotFoundException,
			SubContractorAlreadyBookedException {
		worker.clearBooking(subContractor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SubContractor getSubContractorByRecNo(int recNo)
			throws RemoteException, SubContractorNotFoundException {
		return worker.getSubContractorByRecNo(recNo);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SubContractor> search(SearchCriteria searchCriteria)
			throws RemoteException, SubContractorNotFoundException {
		return worker.search(searchCriteria);
	}

}
