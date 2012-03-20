package suncertify.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import suncertify.Util;
import suncertify.domain.SubContractor;
import suncertify.domain.SubContractorHandler;

/**
 * This modal Dialog displays the detail information of a subcontractor.
 * Depending on the main window's size, the result <code>JTable</code> may hide
 * some information due to limited column width. A double click on a list
 * element opens this dialog. For shortcut reasons, booking and clear booking
 * buttons are also available in this dialog.
 * 
 * @author Jochen R. Meyer
 * 
 */
public class DetailDialog extends JDialog {

	/** The serial version of this class */
	private static final long serialVersionUID = 1L;

	/** A reference to the subcontractor whose information is displayed */
	private SubContractor subContractor;

	/**
	 * Constructs the Dialog using the specified subcontractor and controller.
	 * 
	 * @param subContractor
	 *            the subcontractor whose detail data shall be displayed
	 * @param control
	 *            the controller to use for handling user input
	 */
	public DetailDialog(SubContractor subContractor,
			SubContractorController control) {
		this.subContractor = subContractor;
		initialize(control);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		super.dispose();
	}

	/**
	 * Initializes the Dialog, arranges its elements and displays it.
	 * 
	 * @param control
	 *            the controller to use for handling user input
	 */
	private void initialize(final SubContractorController control) {
		Util util = Util.getInstance();
		setTitle("Booking agent");
		Dimension dialogDim = new Dimension(350, 265);
		setSize(dialogDim);
		setLocation(util.getCenteredWindowPosition(dialogDim));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		JPanel panMain = new JPanel(new GridBagLayout());
		panMain.setBorder(util
				.getStdTitledBorder("Home improvement contractor's detail"));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(3, 6, 3, 6);

		String[] headers = new SubContractorModel().getHeaders();

		// Field names
		JLabel label = new JLabel(headers[0] + ":"); // Name
		gbc.gridx = 0;
		gbc.gridy = 0;
		panMain.add(label, gbc);
		label = new JLabel(headers[1] + ":"); // Location
		gbc.gridx = 0;
		gbc.gridy = 1;
		panMain.add(label, gbc);
		label = new JLabel(headers[2] + ":"); // Specialties
		gbc.gridx = 0;
		gbc.gridy = 2;
		panMain.add(label, gbc);
		label = new JLabel(headers[3] + ":"); // Size
		gbc.gridx = 0;
		gbc.gridy = 3;
		panMain.add(label, gbc);
		label = new JLabel(headers[4] + ":"); // Rate
		gbc.gridx = 0;
		gbc.gridy = 4;
		panMain.add(label, gbc);
		label = new JLabel(headers[5] + ":"); // Customer
		gbc.gridx = 0;
		gbc.gridy = 5;
		panMain.add(label, gbc);

		// Field values
		gbc.fill = GridBagConstraints.HORIZONTAL;
		label = new JLabel(subContractor.getName());
		gbc.gridx = 1;
		gbc.gridy = 0;
		panMain.add(label, gbc);
		label = new JLabel(subContractor.getLocation());
		gbc.gridx = 1;
		gbc.gridy = 1;
		panMain.add(label, gbc);
		label = new JLabel(subContractor.getSpecialties());
		gbc.gridx = 1;
		gbc.gridy = 2;
		panMain.add(label, gbc);
		label = new JLabel(subContractor.getSize());
		gbc.gridx = 1;
		gbc.gridy = 3;
		panMain.add(label, gbc);
		label = new JLabel(subContractor.getRate());
		gbc.gridx = 1;
		gbc.gridy = 4;
		panMain.add(label, gbc);
		label = new JLabel(subContractor.getCustomer());
		gbc.gridx = 1;
		gbc.gridy = 5;
		panMain.add(label, gbc);

		// The Buttons
		JPanel panBooking = new JPanel(new FlowLayout(FlowLayout.TRAILING));

		JButton buClearBook = new JButton("Clear booking");
		buClearBook.setToolTipText("Clear the selected SubContractor's "
				+ "booking.");
		buClearBook.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				control.clearBooking(subContractor);
				dispose();
			}
		});
		panBooking.add(buClearBook);

		JButton buBook = new JButton("Book");
		buBook.setToolTipText("Book the selected SubContractor for a "
				+ "customer.");
		buBook.setPreferredSize(buClearBook.getPreferredSize());
		buBook.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				control.book(subContractor);
				dispose();
			}
		});
		buBook.setPreferredSize(buClearBook.getPreferredSize());
		panBooking.add(buBook);

		SubContractorHandler scHandler = new SubContractorHandler();
		if (scHandler.isSubContractorBooked(subContractor)) {
			buBook.setEnabled(false);
		} else {
			buClearBook.setEnabled(false);
		}

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.gridwidth = 2;
		gbc.weighty = 1.0;
		gbc.insets = new Insets(3, 3, 3, 8);
		panMain.add(panBooking, gbc);

		JButton buBack = new JButton("Back");
		buBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 6;
		gbc.weighty = 1.0;
		gbc.insets = new Insets(3, 0, 3, 3);
		panMain.add(buBack, gbc);

		setContentPane(panMain);
		pack();
		setModal(true);
		setVisible(true);
	}

}
