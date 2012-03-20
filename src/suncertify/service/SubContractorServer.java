package suncertify.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import suncertify.domain.SubContractor;

/**
 * This interface is the server side service interface and defines the same
 * functionality for the server-client communication as
 * <code>SubContractorService</code>. The difference is, that the server can
 * additionally throw a <code>RemoteException</code>. This is the reason for
 * defining an extra server service interface.
 * 
 * @author Jochen R. Meyer
 * 
 */
public interface SubContractorServer extends Remote {

	/**
	 * Books the specified subcontractor for the specified customer (owner).
	 * 
	 * @param subContractor
	 *            the subcontractor to book
	 * @param customer
	 *            the customer who booked the subcontractor
	 * @throws RemoteException
	 *             if a server-client communication error occurs
	 * @throws SubContractorNotFoundException
	 *             if the specified subcontractor could not be found in the
	 *             database
	 * @throws SubContractorAlreadyBookedException
	 *             if the specified subcontractor is already booked by another
	 *             customer
	 */
	public void book(SubContractor subContractor, String customer)
			throws RemoteException, SubContractorNotFoundException,
			SubContractorAlreadyBookedException;

	/**
	 * Clears the booking of the specified subcontractor
	 * 
	 * @param subContractor
	 *            the subcontractor whose booking shall be cleard
	 * @throws RemoteException
	 *             if a server-client communication error occurs
	 * @throws SubContractorNotFoundException
	 *             if the specified subcontractor could not be found in the
	 *             database
	 * @throws SubContractorAlreadyBookedException
	 *             if the specified subcontractor has been booked by another
	 *             customer in the meantime
	 */
	public void clearBooking(SubContractor subContractor)
			throws RemoteException, SubContractorNotFoundException,
			SubContractorAlreadyBookedException;

	/**
	 * Retrieves the record with the specified record number from the database.
	 * 
	 * @param recNo
	 *            the number of the record to retrieve from the database
	 * @return a <code>SubContractor</code> object representing the database
	 *         record with the specified number
	 * @throws RemoteException
	 *             if a server-client communication error occurs
	 * @throws SubContractorNotFoundException
	 *             if the specified subcontractor could not be found in the
	 *             database
	 */
	public SubContractor getSubContractorByRecNo(int recNo)
			throws RemoteException, SubContractorNotFoundException;

	/**
	 * Searches in the database for subcontractors matching the given
	 * <code>SearchCriteria</code>.
	 * 
	 * @param searchCriteria
	 *            the criteria to use for the search
	 * @return a <code>List</code> containing all subcontractors matching the
	 *         given search criteria.
	 * @throws RemoteException
	 *             if a server-client communication error occurs
	 * @throws SubContractorNotFoundException
	 *             if the specified subcontractor could not be found in the
	 *             database
	 */
	public List<SubContractor> search(SearchCriteria searchCriteria)
			throws RemoteException, SubContractorNotFoundException;

}
