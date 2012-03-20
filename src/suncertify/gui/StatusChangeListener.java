package suncertify.gui;

/**
 * This interface defines the methods to use when reacting on status changes of
 * the application. Each view implementing this interface and added to the
 * notifier's listeners can act on the change events fired by the notifier.
 * 
 * @author Jochen R. Meyer
 * 
 */
public interface StatusChangeListener {

	/**
	 * Executed if there may be a change whether the selected element is
	 * bookable or not. The specified value is the new value to act on.
	 * 
	 * @param bookable
	 *            <code>true</code> if the new status is 'bookable',
	 *            <code>false</code> otherwise.
	 */
	public void bookableChanged(Boolean bookable);

	/**
	 * Executed if the connection changed. The specified status info is the new
	 * value to be displayed.
	 * 
	 * @param newConnection
	 *            the new connection info
	 */
	public void connectionChanged(String newConnection);

	/**
	 * Executed if the possibility of a refresh changed. The specified value is
	 * the new value to act on.
	 * 
	 * @param refreshable
	 *            <code>true</code> if a refresh is possible now,
	 *            <code>false</code> otherwise
	 */
	public void refreshableChanged(boolean refreshable);

	/**
	 * Executed if the status info changed. The specified status info is the new
	 * value to be displayed.
	 * 
	 * @param newStatus
	 *            the new status info
	 */
	public void statusInfoChanged(String newStatus);

}
