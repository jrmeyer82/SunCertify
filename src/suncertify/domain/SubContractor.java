package suncertify.domain;

import java.io.Serializable;

/**
 * 
 * A <code>SubContractor</code> object represents one database entry. It cannot
 * be instantiated directly. All objects are created by reading it from the
 * database using the <code>SubContractorHandler</code> class. <br>
 * Once a <code>SubContractor</code> is created it can be manipulated by using
 * the <code>public</code> set[attribute]() methods. Even if currently the 
 * customer is the only attribute to be changed by the application, the class is
 * prepared to provide more extended changement possibilities for a
 * <code>SubContractor</code>. <br>
 * This class only provides read/write access to the database fields and the
 * <code>equals()</code> and <code>hashcode()</code> methods. No additional
 * functionality is implemented. <br>
 * The class implements <code>Serializable</code> because their objects need to
 * be used in RMI communication.
 * 
 * @author Jochen R. Meyer
 * 
 */
public class SubContractor implements Serializable {

	/** The serial version of this class */
	private static final long serialVersionUID = 1L;

	/** The record number of the record. Has only a technical purpose and will
	 * not be visible to the user. */
	private int recNo;

	/** The subcontractor's name */
	private String name;

	/** The subcontractor's home city */
	private String location;

	/** The works the subcontractor can do */
	private String specialties;

	/** The number of the subcontractor's employees */
	private String size;

	/** The subcontractor's hourly charge */
	private String rate;

	/** The ID of the customer who booked the subcontractor */
	private String customer;

	/**
	 * Contructs the object without any arguments
	 */
	SubContractor() {

	}

	/**
	 * {@inheritDoc}
	 */	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		SubContractor other = (SubContractor) obj;
		if (location == null) {
			if (other.location != null) {
				return false;
			}
		} else if (!location.equals(other.location)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (rate == null) {
			if (other.rate != null) {
				return false;
			}
		} else if (!rate.equals(other.rate)) {
			return false;
		}
		if (size == null) {
			if (other.size != null) {
				return false;
			}
		} else if (!size.equals(other.size)) {
			return false;
		}
		if (specialties == null) {
			if (other.specialties != null) {
				return false;
			}
		} else if (!specialties.equals(other.specialties)) {
			return false;
		}
		return true;
	}

	/**
	 * Get the ID of the customer who booked the subcontractor
	 * 
	 * @return the ID of the customer who booked the subcontractor
	 */
	public String getCustomer() {
		return customer;
	}

	/**
	 * Get the subcontractor's home city.
	 * 
	 * @return the subcontractor's home city
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Get the subcontractor's name.
	 * 
	 * @return the subcontractor's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the subcontractor's hourly charge
	 * 
	 * @return the subcontractor's hourly charge
	 */
	public String getRate() {
		return rate;
	}

	/**
	 * Get the database record number of the subcontractor.
	 * 
	 * @return the database record number
	 */
	public int getRecNo() {
		return recNo;
	}

	/**
	 * Get the number of the subcontractor's employees
	 * 
	 * @return the number of the subcontractor's employees
	 */
	public String getSize() {
		return size;
	}

	/**
	 * Get the works the subcontractor can do
	 * 
	 * @return the works the subcontractor can do
	 */
	public String getSpecialties() {
		return specialties;
	}

	/**
	 * {@inheritDoc}
	 */	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((rate == null) ? 0 : rate.hashCode());
		result = prime * result + ((size == null) ? 0 : size.hashCode());
		result = prime * result
				+ ((specialties == null) ? 0 : specialties.hashCode());
		return result;
	}

	/**
	 * Set the ID of the customer who booked the subcontractor
	 * 
	 * @param customer
	 *            the ID of the customer who booked the subcontractor
	 */
	public void setCustomer(String customer) {
		this.customer = customer;
	}

	/**
	 * Set the subcontractor's home city.
	 * 
	 * @param location
	 *            the subcontractor's home city
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * Set the subcontractor's name.
	 * 
	 * @param name
	 *            the subcontractor's name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Set the subcontractor's hourly charge
	 * 
	 * @param rate
	 *            the subcontractor's hourly charge
	 */
	public void setRate(String rate) {
		this.rate = rate;
	}

	/**
	 * Set the database record number of the subcontractor.
	 * 
	 * @param recNo
	 *            the database record number
	 */
	public void setRecNo(int recNo) {
		this.recNo = recNo;
	}

	/**
	 * Set the number of the subcontractor's employees
	 * 
	 * @param size
	 *            the number of the subcontractor's employees
	 */
	public void setSize(String size) {
		this.size = size;
	}

	/**
	 * Set the works the subcontractor can do
	 * 
	 * @param specialties
	 *            the works the subcontractor can do
	 */
	public void setSpecialties(String specialties) {
		this.specialties = specialties;
	}
	
	

}
