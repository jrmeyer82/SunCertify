package suncertify.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import suncertify.domain.SubContractor;

/**
 * 
 * The <code>SubContractorModel</code> is the 'M' in the MVC system, i.e.: it
 * stores the data which is displayed by the view. <br>
 * There is no new implemented 'modelChanged()' method available because the
 * super class <code>AbstractTableModel</code> provides the
 * <code>fireTableDataChanged()</code> method which is completely suitable to
 * notify the view.
 * <p>
 * By extending <code>AbstractTableModel</code> the functionality to serve as a
 * <code>TableModel</code> is automatically given. Jusst a view adaptions are
 * needed.
 * 
 * @author Jochen R. Meyer
 * 
 */
public class SubContractorModel extends AbstractTableModel {

	/** The serial version of this class */
	private static final long serialVersionUID = 1L;

	/** The headers to be used for the result table */
	private static final String[] HEADERS = new String[] { "Name", "City",
			"Specialities", "Number of Employees", "Charge per hour",
			"Current customer nr." };

	/**
	 * Get the number of headers (= the number of columns).
	 * 
	 * @return the number of headers
	 */
	public static int getHeaderCount() {
		return HEADERS.length;
	}

	/** The <code>List</code> of the subcontractor elements to display */
	private List<SubContractor> elements;

	/**
	 * Constructs the object without any arguments
	 */
	public SubContractorModel() {
		elements = new ArrayList<SubContractor>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getColumnCount() {
		return HEADERS.length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getColumnName(int column) {
		return HEADERS[column];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getRowCount() {
		return elements.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		SubContractor sc = (SubContractor) elements.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return sc.getName();
		case 1:
			return sc.getLocation();
		case 2:
			return sc.getSpecialties();
		case 3:
			return sc.getSize();
		case 4:
			return sc.getRate();
		case 5:
			return sc.getCustomer();
		default:
			throw new IllegalArgumentException("Unsupported column index '"
					+ columnIndex + "' for " + getClass());
		}
	}

	/**
	 * Deletes all subcontractor elements from the model.
	 */
	void clearElements() {
		elements = new ArrayList<SubContractor>();
	}

	/**
	 * Returns the element at the specified index in the subcontractor element
	 * list.
	 * 
	 * @param index
	 *            the index of the wanted subcontractor element
	 * @return the subcontractor element at the specified index.
	 */
	SubContractor getElementAt(int index) {
		return elements.get(index);
	}

	/**
	 * Returns the number of subcontractor elements the model contains.
	 * 
	 * @return the number of subcontractor elements the model contains
	 */
	int getElementCount() {
		return elements.size();
	}

	/**
	 * Returns a <code>String</code> array containing the headers.
	 * 
	 * @return a <code>String</code> array containing the headers
	 */
	String[] getHeaders() {
		return HEADERS;
	}

	/**
	 * Removes the specified subcontractor element from the element list.
	 * 
	 * @param subContractor
	 *            the subcontractor element to remove
	 */
	void removeElement(SubContractor subContractor) {
		elements.remove(subContractor);
	}

	/**
	 * Replaces the first specified subcontractor element by the second
	 * specified subcontractor element.
	 * 
	 * @param subContractorOld
	 *            the subcontractor element to replace
	 * @param subContractorNew
	 *            the replacement
	 */
	void replaceElement(SubContractor subContractorOld, 
			SubContractor subContractorNew) {
		int index = elements.indexOf(subContractorOld);
		elements.remove(index);
		elements.add(index, subContractorNew);
	}

	/**
	 * Replaces all subcontractor elements (if present) by the elements in the
	 * specified list.
	 * 
	 * @param elements
	 *            the new elements for the model
	 */
	void setElements(List<SubContractor> elements) {
		this.elements = elements;
	}

}
