package suncertify.service;

import java.io.Serializable;

import suncertify.Util;
import suncertify.gui.SubContractorModel;

/**
 * This class encapsulates the criteria to use when searching for subcontractors
 * on the database. All supported fields are set one by one by the provided
 * setter methods. This means, it can be searched only for a criteria which can
 * be set by a setter method in this class. <br>
 * The <code>Serializable</code> interface must be implemented because
 * <code>SearchCriteria</code> objects are transferred over RMI as method
 * arguments.
 * 
 * @author Jochen R. Meyer
 * 
 */
public class SearchCriteria implements Serializable {

	/** The serial version of this class */
	private static final long serialVersionUID = 1L;

	/** The position of the location in the criteria array */
	private static final int POS_LOCATION = 1;

	/** The position of the name in the criteria array */
	private static final int POS_NAME = 0;
	
	/** The <code>Util</code> instance to access some convenience and helping
	 * methods */
	private static final Util UTIL = Util.getInstance();

	/** Indicates if the search criteria should be combined with 'and' or 'or'.
	 * Standard value is <code>true</code> what means 'and'. */
	private boolean searchAnd = true;

	/** An array containing the search criteria. Each array element maps to a
	 * database field / <code>SubContractor</code> attribute. */
	private String[] criteria;

	/**
	 * Contructs the object without any arguments
	 */
	public SearchCriteria() {
		criteria = new String[SubContractorModel.getHeaderCount()];
	}

	/**
	 * Returns the search criteria as an array of <code>String</code> objects.
	 * 
	 * @return the search criteria
	 */
	public String[] getCriteria() {
		return criteria;
	}

	/**
	 * Gets the search criteria value for the subcontractor's home city.
	 * 
	 * @return the search criteria value for the subcontractor's home city
	 */
	public String getLocation() {
		return criteria[POS_LOCATION];
	}

	/**
	 * Gets the search criteria value for the subcontractor's name.
	 * 
	 * @return the search criteria value for the the subcontractor's name
	 */
	public String getName() {
		return criteria[POS_NAME];
	}

	/**
	 * Indicates if the search criteria should be combined by 'and' or 'or'.
	 * 
	 * @return <code>true</code> if the search criteria will be combined by
	 *         'and', <code>false</code> if the search criteria will be combined
	 *         by 'or'
	 */
	public boolean isSearchAnd() {
		return searchAnd;
	}
	
	/**
	 * Sets the search criteria value for the subcontractor's home city.
	 * 
	 * @param location
	 *            the search criteria value for the subcontractor's home city
	 */
	public void setLocation(String location) {
		criteria[POS_LOCATION] = location;
	}

	/**
	 * Sets the search criteria value for the subcontractor's name.
	 * 
	 * @param name
	 *            the search criteria value for the the subcontractor's name
	 */
	public void setName(String name) {
		criteria[POS_NAME] = name;
	}

	/**
	 * Specifies if the search criteria should be combined by 'and'
	 * 
	 * @param searchAnd
	 *            <code>true</code> if the search criteria should be combined by
	 *            'and', <code>false</code> if the search criteria should be
	 *            combined by 'or'
	 */
	public void setSearchAnd(boolean searchAnd) {
		this.searchAnd = searchAnd;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {

		String name = criteria[POS_NAME];
		String loc = criteria[POS_LOCATION];
		String op = (searchAnd ? "AND" : "OR");

		String str = (UTIL.isStringEmptyOrNull(name) ? "" : "name = '" + name
				+ "' ")
				+ (UTIL.isStringEmptyOrNull(name)
						|| UTIL.isStringEmptyOrNull(loc) ? "" : " " + op + " ")
				+ (UTIL.isStringEmptyOrNull(loc) ? "" : "location = '" + loc
						+ "' ");

		return UTIL.isStringEmptyOrNull(str) ? "<all>" : str;
	}
}
