package suncertify.domain;

/**
 * The <code>SubContractorHanlder</code> class was built to create
 * <code>SubContractor</code> objects (i.e.: map database elements to java
 * objects) and provide some additional functionality for their usage.
 * 
 * @author Jochen R. Meyer
 * 
 */
public class SubContractorHandler {

	/**
	 * Contructs the object without any arguments
	 */
	public SubContractorHandler() {

	}

	/**
	 * Create a <code>SubContractor</code> from the given record number and data
	 * array coming from the database.
	 * 
	 * @param recNo
	 *            the number of the record in the database
	 * @param data
	 *            the record's data
	 * @return a <code>SubContractor</code> object containing the specified
	 *         record number and data
	 */
	public SubContractor createSubContractor(int recNo, String[] data) {
		SubContractor sc = new SubContractor();
		sc.setRecNo(recNo);
		sc.setName(data[0]);
		sc.setLocation(data[1]);
		sc.setSpecialties(data[2]);
		sc.setSize(data[3]);
		sc.setRate(data[4]);
		sc.setCustomer(data[5]);
		return sc;
	}

	/**
	 * Formats the given <code>SubContractor</code> object to a readable
	 * <code>String</code>.
	 * 
	 * @param subContractor
	 *            the <code>SubContractor</code> to format
	 * @return the String representation of the given <code>SubContractor</code>
	 */
	public String format(SubContractor subContractor) {
		return subContractor.getName() + " from " + subContractor.getLocation()
				+ ", " + "specialties: " + subContractor.getSpecialties()
				+ " (" + subContractor.getSize() + " / "
				+ subContractor.getRate() + "), booked by '"
				+ subContractor.getCustomer() + "'";
	}

	/**
	 * Returns a message indicating what <code>String</code> is valid to be set
	 * as a <code>SubContractor</code>'s customer.
	 * 
	 * @return the message with the information what is a valid
	 *         <code>SubContractor</code>'s customer
	 */
	public String getValidCustomerInfo() {
		return "exactly 8 digits";
	}

	/**
	 * Checks if a <code>SubContractor</code> is booked.
	 * 
	 * @param subContractor
	 *            the <code>SubContractor</code> to check
	 * @return <code>true</code> if the <code>SubContractor</code> is booked,
	 *         <code>false</code> otherwise
	 */
	public boolean isSubContractorBooked(SubContractor subContractor) {
		return subContractor.getClass() != null
				&& subContractor.getCustomer().length() > 0;
	}

	/**
	 * Checks if a the given <code>String</code> could be used as an customer
	 * for a <code>SubContractor</code>.
	 * 
	 * @param customer
	 *            the values to check
	 * @return <code>true</code> if the given <code>String</code> would be a
	 *         valid customer for a <code>SubContractor</code>,
	 *         <code>false</code> otherwise.
	 */
	public boolean isValidCustomer(String customer) {
		return customer != null && customer.matches("\\d{8}");
	}

	/**
	 * Transforms the <code>SubContractor</code>'s data to a <code>String</code>
	 * array and returns it.
	 * 
	 * @param subContractor
	 *            the <code>SubContractor</code> to transform
	 * @return a <code>String</code> containing the given
	 *         <code>SubContractor</code>'s data
	 */
	public String[] transformSubContractorToArray(SubContractor subContractor) {
		return new String[] { subContractor.getName(),
				subContractor.getLocation(), subContractor.getSpecialties(),
				subContractor.getSize(), subContractor.getRate(),
				subContractor.getCustomer() };
	}

}
