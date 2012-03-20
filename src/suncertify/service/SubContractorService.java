package suncertify.service;

import java.util.List;

import suncertify.domain.SubContractor;

/**
 * This interface is the client side service interface and defines the same
 * functionality for the server-client communication as
 * <code>SubContractorServer</code>. The difference is, that a client cannot
 * throw a <code>RemoteException</code> and is thus not forced to handle it.<br>
 * In contrary to <code>SubContractorServer</code>,
 * <code>SubContractorService</code> has one additional mehtod to provide a
 * connection info.
 * 
 * @author Jochen R. Meyer
 * 
 */
public interface SubContractorService {

	/**
	 * Books the specified subcontractor for the specified customer (owner).
	 * 
	 * @param subContractor
	 *            the subcontractor to book
	 * @param customer
	 *            the customer who booked the subcontractor
	 * @throws SubContractorNotFoundException
	 *             if the specified subcontractor could not be found in the
	 *             database
	 * @throws SubContractorAlreadyBookedException
	 *             if the specified subcontractor is already booked by another
	 *             customer
	 */
	public void book(SubContractor subContractor, String customer)
			throws SubContractorNotFoundException,
			SubContractorAlreadyBookedException;

	/**
	 * Clears the booking of the specified subcontractor
	 * 
	 * @param subContractor
	 *            the subcontractor whose booking shall be cleard
	 * @throws SubContractorNotFoundException
	 *             if the specified subcontractor could not be found in the
	 *             database
	 * @throws SubContractorAlreadyBookedException
	 *             if the specified subcontractor has been booked by another
	 *             customer in the meantime
	 */
	public void clearBooking(SubContractor subContractor)
			throws SubContractorNotFoundException,
			SubContractorAlreadyBookedException;

	/**
	 * Provides a connection info <code>String</code> indicating the connection
	 * currently in use.
	 * 
	 * @return the connection currently in use
	 */
	public String getConnectionAsString();

	/**
	 * Retrieves the record with the specified record number from the database.
	 * 
	 * @param recNo
	 *            the number of the record to retrieve from the database
	 * @return a <code>SubContractor</code> object representing the database
	 *         record with the specified number
	 * @throws SubContractorNotFoundException
	 *             if the specified subcontractor could not be found in the
	 *             database
	 */

	public SubContractor getSubContractorByRecNo(int recNo)
			throws SubContractorNotFoundException;

	/**
	 * Searches in the database for subcontractors matching the given
	 * <code>SearchCriteria</code>.
	 * 
	 * @param searchCriteria
	 *            the criteria to use for the search
	 * @return a <code>List</code> containing all subcontractors matching the
	 *         given search criteria.
	 * @throws SubContractorNotFoundException
	 *             if the specified subcontractor could not be found in the
	 *             database
	 */
	public List<SubContractor> search(SearchCriteria searchCriteria)
			throws SubContractorNotFoundException;

}
