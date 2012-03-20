package suncertify.service;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Logger;

import suncertify.Util;
import suncertify.domain.SubContractor;

/**
 * This class is the client side implementation of the client-server system. All
 * method calls are passed to its <code>SubContractorServer</code> instance to
 * be executed on the server.
 * 
 * @author Jochen R. Meyer
 * 
 */
public class SubContractorServiceClient implements SubContractorService {

	/** A standard connection error message */
	private static final String CONNECTION_ERROR = "Could not get "
			+ "SubContractor from server due to a connection error.";

	/** The standard logger instance */
	private static final Logger LOGGER = Util.getInstance().getStdLogger();

	/** The server instance the client is connected to */
	private SubContractorServer server;

	/** The url used to connect to the server */
	private String connectionUrl;

	/**
	 * Constructs the object using the specified url.
	 * 
	 * @param connectionUrl
	 *            the url of the server to connect to
	 * @throws RemoteException
	 *             if a server-client communication error occurs
	 */
	SubContractorServiceClient(String connectionUrl) throws RemoteException {
		this.connectionUrl = connectionUrl;
		LOGGER.info("Connecting to '" + connectionUrl + "'...");
		try {
			server = (SubContractorServer) Naming.lookup(connectionUrl);
		} catch (MalformedURLException mue) {
			throw new RemoteException("The URL '" + connectionUrl
					+ "' is malformed", mue);
		} catch (NotBoundException nbe) {
			throw new RemoteException("The URL '" + connectionUrl
					+ "' is not bound", nbe);
		}
		LOGGER.info("Connection established");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void book(SubContractor subContractor, String customer)
			throws SubContractorNotFoundException,
			SubContractorAlreadyBookedException {
		try {
			server.book(subContractor, customer);
		} catch (RemoteException e) {
			throw new SubContractorNotFoundException(CONNECTION_ERROR, e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearBooking(SubContractor subContractor)
			throws SubContractorNotFoundException,
			SubContractorAlreadyBookedException {
		try {
			server.clearBooking(subContractor);
		} catch (RemoteException e) {
			throw new SubContractorNotFoundException(CONNECTION_ERROR, e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getConnectionAsString() {
		return "Connected to server: " + connectionUrl;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SubContractor getSubContractorByRecNo(int recNo)
			throws SubContractorNotFoundException {
		try {
			return server.getSubContractorByRecNo(recNo);
		} catch (RemoteException e) {
			throw new SubContractorNotFoundException(CONNECTION_ERROR, e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SubContractor> search(SearchCriteria searchCriteria)
			throws SubContractorNotFoundException {
		try {
			return server.search(searchCriteria);
		} catch (RemoteException e) {
			throw new SubContractorNotFoundException(CONNECTION_ERROR, e);
		}
	}

}
