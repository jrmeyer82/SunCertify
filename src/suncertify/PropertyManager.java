package suncertify;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This singleton class provides access to the file 'suncertify.properties'. If
 * this file does not exist (at the first application start) it is created. The
 * value of the RMI port is predefined to RMI's standard port '1099'. <br>
 * The class provides public <code>String</code> constants which can (and
 * should) be used to access the different available properties. <br>
 * Internally, <code>PropertyManager</code> uses a <code>Properties</code>
 * object which provides full functionality for a read/write access to a
 * properties file. The <code>PropertyManager</code> adapts and limits the
 * <code>Properties</code> object's functionality to be easy to use and
 * understand while matching all requirements.
 * 
 * @author Jochen R. Meyer
 * 
 */
public final class PropertyManager {

	/** The absolute path of the database file */
	public static final String PROPERTY_DB_FILE_PATH = "database.file.path";

	/** The RMI server address (name or IP address) */
	public static final String PROPERTY_SERVER_ADDRESS = "server.address";

	/** The RMI server's port */
	public static final String PROPERTY_SERVER_PORT = "server.port";

	/** Standard value of the RMI port */
	private static final String SERVER_PORT_STANDARD = "1099";

	/** The instance to read/write from/to the database */
	private Properties props;

	/** The <code>PropertyManager</code> instance */
	private static PropertyManager propMan = new PropertyManager();

	/** The standard property file name */
	private static final String PROP_FILE = "suncertify.properties";

	/**
	 * Returns the <code>PropertyManager</code> instance.
	 * 
	 * @return the <code>PropertyManager</code> instance
	 */
	public static PropertyManager getInstance() {
		return propMan;
	}

	/**
	 * Constructs the object without any arguments. <br>
	 * If no properties file exists, it is created.
	 */
	private PropertyManager() {
		try {
			props = new Properties();
			File propFile = new File(PROP_FILE);

			/*
			 * If no properties file is available (at first program start),
			 * create it and initialize it with all known standard values
			 */
			if (!propFile.exists()) {
				props.setProperty(PROPERTY_DB_FILE_PATH, "");
				props.setProperty(PROPERTY_SERVER_ADDRESS, "");
				props.setProperty(PROPERTY_SERVER_PORT, SERVER_PORT_STANDARD);
				props.store(new FileWriter(PROP_FILE), "Property '" + PROP_FILE
						+ "' created and initialized");
			}

			props.load(new FileInputStream(propFile));
		} catch (IOException ioe) {

			/*
			 * Program is not usable properly without a valid PropertyManager.
			 * Thus: Shutdown if its creation fails
			 */
			Logger logger = Util.getInstance().getStdLogger();
			logger.log(Level.SEVERE, "Could not initialize PropertyManager"
					+ Util.getInstance().newLine() + "Program shutdown", ioe);
			System.exit(1);
		}
	}

	/**
	 * Checks, if the specified property is available.
	 * 
	 * @param property
	 *            the property to check
	 * @return <code>true</code> if the specified proerpty is avialble,
	 *         <code>false</code> otherwise
	 */
	public boolean containsProperty(String property) {
		return props.containsKey(property);
	}

	/**
	 * Returns all available properties.
	 * 
	 * @return all available properties
	 */
	public List<String> getProperties() {
		ArrayList<String> propis = new ArrayList<String>();
		Enumeration<?> propKeys = props.propertyNames();
		while (propKeys.hasMoreElements()) {
			propis.add(propKeys.nextElement().toString());
		}
		return propis;
	}

	/**
	 * Returns the value of the specified property.
	 * 
	 * @param property
	 *            the property to get the value from
	 * @return the property value
	 */
	public String getProperty(String property) {
		return props.getProperty(property);
	}

	/**
	 * Sets the value of the specified property to the specified value and
	 * writes it directly to the properties file.
	 * 
	 * @param property
	 *            the property to set
	 * @param value
	 *            the value to set
	 * @throws IOException
	 *             if a file write error occurs
	 */
	public void setProperty(String property, String value) throws IOException {
		props.setProperty(property, value);
		props.store(new FileWriter(PROP_FILE), "Property '" + property
				+ "' saved");
	}

}
