package suncertify.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;

import suncertify.Util;
import suncertify.Util.ProgramMode;
import suncertify.domain.SubContractor;
import suncertify.service.SearchCriteria;

/**
 * The main GUI element. It shows the table containing the subcontractors (in
 * fact the 'V' in MVC) and interaction elements for the searching and the
 * booking. <br>
 * Also visible is a status bar, a label indicating the current connection and
 * the menu bar to change the connection and exit the application. By
 * implementing the <code>StatusChangeListener</code> interface these elements
 * are kept up to date.
 * 
 * @author Jochen R. Meyer
 * 
 */
public class MainFrame extends JFrame implements StatusChangeListener {

	/** The serial version of this class */
	private static final long serialVersionUID = 1L;

	/** The table to display the subcontractors */
	private JTable tabResult;

	/** The <code>Util</code> instance to access its convenience and helping
	 * methods */
	private static final Util UTIL = Util.getInstance();

	/** A reference to the model */
	private SubContractorModel model;

	/** A reference to the controller */
	private SubContractorController control;

	/** The text input field for the subcontractor's name */
	private JTextField tfName;

	/** The text input field for the subcontractor's home city */
	private JTextField tfLocation;

	/** The radio button indicating an "AND" search */
	private JRadioButton rBuAnd;

	/** The label indicating the connection info */
	private JLabel laConnectionInfo;

	/** The label indicating the current application status */
	private JLabel laStatusInfo;

	/** The button to book a subcontractor */
	private JButton buBook;

	/** The button to clear a subcontractor's booking */
	private JButton buClearBook;

	/** The button to show a subcontractor's detail */
	private JButton buDetail;

	/** The button to refresh the table, i.e.: execute the last search again */
	private JButton buRefresh;

	/**
	 * Constructs the frame using the specified model and controller.
	 * 
	 * @param model
	 *            the model which data is displayed
	 * @param control
	 *            the controller which handles the user input
	 */
	public MainFrame(SubContractorModel model, SubContractorController control) {
		this.model = model;
		this.control = control;
		this.control.addApplicationStatusListener(this);
		initialize();
		control.fireStateChangedConnection();
		control.fireStateChangedStatusInfo("Application started");

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void bookableChanged(Boolean bookable) {
		if (bookable == null) {
			buBook.setEnabled(false);
			buClearBook.setEnabled(false);
			buDetail.setEnabled(false);
		} else {
			buBook.setEnabled(bookable);
			buClearBook.setEnabled(!bookable);
			buDetail.setEnabled(true);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void connectionChanged(String newConnection) {

		// Make all columns unsorted again
		@SuppressWarnings("unchecked")
		TableRowSorter<SubContractorModel> sorter = 
				(TableRowSorter<SubContractorModel>) tabResult.getRowSorter();
		for (int c = 0; c < tabResult.getModel().getColumnCount(); c++) {
			sorter.setSortable(c, false);
		}
		tabResult.setAutoCreateRowSorter(true);

		laConnectionInfo.setText(newConnection);
		tfName.setText("");
		tfLocation.setText("");
	}

	/**
	 * Does not exit the frame directly, but uses the controller to display a
	 * confirmation message to the user.
	 */
	@Override
	public void dispose() {
		control.reallyExitApplication();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refreshableChanged(boolean refreshable) {
		
		// Refresh button only availbale in client mode
		if (buRefresh != null) {
			buRefresh.setEnabled(refreshable);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void statusInfoChanged(String newStatus) {
		if (UTIL.isStringEmptyOrNull(newStatus)) {
			
			/*
			 * Prevent status label panel from changing size in case of an empty
			 * String.
			 */
			laStatusInfo.setText(" ");
		} else {
			laStatusInfo.setText(newStatus);
		}

	}

	/**
	 * Creates and returns a panel containing all elements to interact with a
	 * subcontractor like show detail and book/clear booking.
	 * 
	 * @return the interaction panel containing all needed buttons
	 */
	private JPanel getActionPanel() {
		JPanel panActionLeft = new JPanel(new FlowLayout(FlowLayout.LEADING));
		buDetail = new JButton("Show detail");
		buDetail.setToolTipText("Show the selected SubContractor's detail.");
		buDetail.setEnabled(false);
		buDetail.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				control.showSubContractorDetail(getSelectedSubContractor());
			}
		});
		panActionLeft.add(buDetail);

		JPanel panActionRight = new JPanel(new FlowLayout(FlowLayout.TRAILING));

		buClearBook = new JButton("Clear booking");
		buClearBook.setToolTipText("Clear the selected SubContractor "
				+ "booking.");
		buClearBook.setEnabled(false);
		buClearBook.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				control.clearBooking(getSelectedSubContractor());
			}
		});
		panActionRight.add(buClearBook);

		buBook = new JButton("Book");
		buBook.setEnabled(false);
		buBook.setToolTipText("Book the selected SubContractor for a "
				+ "customer.");
		buBook.setPreferredSize(buClearBook.getPreferredSize());
		buBook.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				control.book(getSelectedSubContractor());
			}
		});
		buBook.setPreferredSize(buClearBook.getPreferredSize());
		panActionRight.add(buBook);

		JPanel panel = new JPanel(new GridLayout());
		panel.add(panActionLeft);
		panel.add(panActionRight);

		return panel;
	}

	/**
	 * Creates and returns a panel containing the connection info element(s).
	 * 
	 * @return the info panel containing all needed elements
	 */
	private JPanel getInfoPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(UTIL.getStdTitledBorder("Connection info"));

		laConnectionInfo = new JLabel();

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.5;
		gbc.insets = new Insets(6, 6, 6, 6);
		panel.add(laConnectionInfo, gbc);

		return panel;
	}

	/**
	 * Creates and returns the menu bar for this frame. Depending on the program
	 * mode (client/standalone), the menu is filled with the needed menu items.
	 * 
	 * @return the program mode dependent menu bar to use in this frame
	 */
	private JMenuBar getMenuBarForCurrentMode() {
		JMenuBar menuBar = new JMenuBar();

		JMenu meFile = new JMenu("File");

		/*
		 * Composing file menu elements depending on current mode 
		 * standalone: User can choose a database file to work on
		 * client: User can input the server/port to connect to
		 */
		if (UTIL.getProgramMode() == ProgramMode.STANDALONE) {
			JMenuItem itemChooseDbFile = new JMenuItem("Choose database file");
			itemChooseDbFile.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					control.chooseDbFile();
				}
			});
			meFile.add(itemChooseDbFile);
		}
		if (UTIL.getProgramMode() == ProgramMode.CLIENT) {
			JMenuItem itemServerAndPort = new JMenuItem("Input server and port");
			itemServerAndPort.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					control.inputServerAndPort();
				}
			});
			meFile.add(itemServerAndPort);
		}

		meFile.add(new JSeparator());

		JMenuItem meFileItemExit = new JMenuItem("Exit");
		meFileItemExit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				control.reallyExitApplication();
			}
		});
		meFile.add(meFileItemExit);

		menuBar.add(meFile);
		return menuBar;
	}

	/**
	 * Collects the specified search criteria, creates a
	 * <code>SearchCriteria</code> object from them and returns it.
	 * 
	 * @return the search criteria specified by the user
	 */
	private SearchCriteria getSearchCriteria() {
		SearchCriteria criteria = new SearchCriteria();
		criteria.setName(tfName.getText().trim());
		criteria.setLocation(tfLocation.getText().trim());
		criteria.setSearchAnd(rBuAnd.isSelected());
		return criteria;
	}

	/**
	 * Creates and returns a panel containing all elements to configure and
	 * execute a search.
	 * 
	 * @return the search panel containing all needed search elements
	 */
	private JPanel getSearchPanel() {

		KeyAdapter keyAdapter = new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					control.executeSearch(getSearchCriteria());
				}
			}
		};

		JPanel panel = new JPanel();
		panel.setBorder(UTIL.getStdTitledBorder("Search"));
		panel.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		JLabel laName = new JLabel("Name");
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(6, 6, 2, 2);
		panel.add(laName, gbc);

		tfName = new JTextField();
		tfName.addKeyListener(keyAdapter);
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 2;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 0.5;
		gbc.insets = new Insets(6, 2, 2, 6);
		panel.add(tfName, gbc);

		JLabel laLocation = new JLabel("Location");
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(2, 6, 2, 6);
		panel.add(laLocation, gbc);

		tfLocation = new JTextField();
		tfLocation.addKeyListener(keyAdapter);
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 2;
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 0.5;
		gbc.insets = new Insets(2, 2, 2, 6);
		panel.add(tfLocation, gbc);

		rBuAnd = new JRadioButton("and");
		rBuAnd.addKeyListener(keyAdapter);
		rBuAnd.setSelected(true); // Initial search criteria is AND
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.insets = new Insets(2, 2, 6, 2);
		panel.add(rBuAnd, gbc);

		JRadioButton rBuOr = new JRadioButton("or");
		rBuOr.addKeyListener(keyAdapter);
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.insets = new Insets(2, 2, 6, 2);
		panel.add(rBuOr, gbc);

		ButtonGroup rBuGroup = new ButtonGroup();
		rBuGroup.add(rBuAnd);
		rBuGroup.add(rBuOr);

		JButton buStartSearch = new JButton("Start Search");
		buStartSearch.setToolTipText("Start the SubContractor search with "
				+ "the specified criteria");
		buStartSearch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				tabResult.clearSelection();
				control.executeSearch(getSearchCriteria());
			}
		});
		buStartSearch.addKeyListener(keyAdapter);

		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.EAST;
		gbc.gridx = 2;
		gbc.gridy = 2;
		gbc.insets = new Insets(2, 2, 6, 6);
		panel.add(buStartSearch, gbc);

		return panel;
	}

	/**
	 * Returns the subcontractor selected in the result table.
	 * 
	 * @return the selected subcontractor
	 */
	private SubContractor getSelectedSubContractor() {
		int selectedRow = tabResult.getSelectedRow();

		// Get the really selected element (needed if the table was sorted)
		int mappedModelIndex = tabResult.getRowSorter().convertRowIndexToModel(
				selectedRow);
		return model.getElementAt(mappedModelIndex);
	}

	/**
	 * Creates and returns a panel containing the status info element(s).
	 * 
	 * @return a panel containing the status info element(s)
	 */
	private JPanel getStatusPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		panel.setBorder(UTIL.getStdTitledBorder("Status info"));

		laStatusInfo = new JLabel();
		panel.add(laStatusInfo);

		return panel;
	}

	/**
	 * Creates and returns a panel containing the scrollable result table with
	 * the subcontractors. Also creates all listeners for the interaction with
	 * the table.
	 * 
	 * @return the panel containing the scrollable result table
	 */
	private JPanel getTablePanel() {
		tabResult = new JTable(model);
		tabResult.setAutoCreateRowSorter(true);

		// Prohibit JTable's line feed operation when typing enter 
		tabResult.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "none");

		tabResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tabResult.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e) {
						if (!e.getValueIsAdjusting()) {
							if (tabResult.getSelectedRow() >= 0) {
								control.fireStateChangedBookable(
										getSelectedSubContractor());
							} else {
								control.fireStateChangedBookable(null);
							}
						}
					}
				});

		tabResult.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {

				// Only care for "left-clicks"
				if (e.getButton() != MouseEvent.BUTTON1) {
					return;
				}
				if (e.getClickCount() == 2) { // system double click
					control.showSubContractorDetail(getSelectedSubContractor());
				} else {
					control.fireStateChangedBookable(getSelectedSubContractor());
				}
			}
		});

		tabResult.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					control.showSubContractorDetail(getSelectedSubContractor());
				}
			}
		});

		JScrollPane spCenter = new JScrollPane();
		spCenter.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		spCenter.setPreferredSize(new Dimension(500, 500));
		spCenter.setViewportView(tabResult);

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(UTIL.getStdTitledBorder("Data"));
		panel.add(spCenter, BorderLayout.CENTER);

		if (Util.getInstance().getProgramMode() == ProgramMode.CLIENT) {
			buRefresh = new JButton("Refresh table");
			buRefresh.setToolTipText("Reload the search result table");
			buRefresh.setEnabled(false);
			buRefresh.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (model.getElementCount() > 0) {
						control.executeLastSearch();
					}
				}
			});

			JPanel panSouth = new JPanel(new FlowLayout(FlowLayout.TRAILING));
			panSouth.add(buRefresh);
			panel.add(panSouth, BorderLayout.SOUTH);
		}

		return panel;
	}

	/**
	 * Initializes the contents of the frame.
	 */
	private void initialize() {
		setTitle("Booking agent - Home improvement contractors");
		setMinimumSize(new Dimension(500, 230));
		Dimension dialogDim = new Dimension(800, 700);
		setSize(dialogDim);
		setLocation(UTIL.getCenteredWindowPosition(dialogDim));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel panNorth = new JPanel(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0.2;
		gbc.gridheight = 2;
		gbc.gridx = 0;
		gbc.gridy = 0;
		panNorth.add(getSearchPanel(), gbc);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0.8;
		gbc.weighty = 1.0;
		gbc.gridx = 1;
		gbc.gridy = 0;
		panNorth.add(getInfoPanel(), gbc);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0.8;
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.insets = new Insets(6, 0, 0, 0);
		panNorth.add(getActionPanel(), gbc);

		JPanel panMain = new JPanel(new BorderLayout());
		panMain.add(panNorth, BorderLayout.NORTH);
		panMain.add(getTablePanel(), BorderLayout.CENTER);
		panMain.add(getStatusPanel(), BorderLayout.SOUTH);

		setContentPane(panMain);

		setJMenuBar(getMenuBarForCurrentMode());

		setVisible(true);
		
		// Display all available data from database 
		control.executeSearch(new SearchCriteria());
	}

}
