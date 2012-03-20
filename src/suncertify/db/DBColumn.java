package suncertify.db;

/**
 * Representation of a database column with its attributes. Because the
 * application uses just a <code>String</code> based file database, there is no
 * type attribute. This can be added easily in future versions.
 * 
 * @author Jochen R. Meyer
 * 
 */
class DBColumn {

	/**	The name of the column */
	private String name;

	/** The length of the column's values */
	private int length;

	/** The column's position number at which position the column resides in the
	 * database */
	private int number;
	
	/** Contructs the object without any arguments. */
	public DBColumn() {
		
	}

	/**
	 * Get the name of the column.
	 * 
	 * @return the name.
	 */
	String getName() {
		return name;
	}

	/**
	 * Set the name of the column.
	 * 
	 * @param name
	 *            the name to set
	 */
	void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the length of the values in the column.
	 * 
	 * @return the length of the column's values
	 */
	int getLength() {
		return length;
	}

	/**
	 * Set the length of the values in the column.
	 * 
	 * @param length
	 *            the length of the column's values
	 */
	void setLength(int length) {
		this.length = length;
	}

	/**
	 * Get the position number of the column.
	 * 
	 * @return the column's position number
	 */
	int getNumber() {
		return number;
	}

	/**
	 * Set the position number of the column.
	 * 
	 * @param number
	 *            the column's position number to set
	 */
	void setNumber(int number) {
		this.number = number;
	}

}
