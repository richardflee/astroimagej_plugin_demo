/*
 * Created by JFormDesigner on Wed Apr 07 08:13:13 BST 2021
 */

package rfl.astroimagej.dev.catalog_ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import rfl.astroimagej.dev._plugin.Vsp_Demo;
import rfl.astroimagej.dev.catalogs.CatalogQuery;
import rfl.astroimagej.dev.enums.CatalogType;
import rfl.astroimagej.dev.enums.TextFieldType;
import rfl.astroimagej.dev.fileio.FileWriterListener;
import rfl.astroimagej.dev.utils.AstroCoords;
import rfl.astroimagej.exceptions.SimbadNotFoundException;

/**
 * User input form to specify and run an online photometry database query. Outputs are:
 * <p> 
 * AstroimageJ compatible radec file, with filename format [target].[filter].[fov_amin].radec.txt 
 * </p> 
 * <p>
 * DSS fits file with filename format: [target].[filter].[fov_amin].fits 
 * </p>
 * <p>
 * Implemented for the AAVSO VSP catalog (2021-05)
 * </p>
 */
public class CatalogFormUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private TextInputVerifier inputVerifier;
	private List<JTextField> textFields;
	private SimbadCatalog simbad;

	private FileWriterListener writePropsListener;
	private FileWriterListener writeRaDecListener;

	private final String TITLE = "Saved current dialog settings to properties file";
	private final String INVALID_DATA = "At least one field has invalid data entry";

	public static void main(String[] args) {
		Vsp_Demo.main(null);
	}

	/**
	 * Opens form to enter VSP target name and related data. User clicks Download to
	 * run on-line query.
	 * 
	 * @param propertiesFileQuery property file parameters
	 */
	public CatalogFormUI(CatalogQuery propertiesFileQuery) {

		// set up form controls
		// form generated in jformdesigner gui builder (GroupLayout)
		initComponents();

		// identify jtextfield objects and set focus order
		configureTextFields();

		// setup text field and button action listeners
		setupActionListeners();

		// verifier for user entry in chart_ui text fields
		inputVerifier = new TextInputVerifier();

		// lookup user object_id against Smbad on-line database
		simbad = new SimbadCatalog();

		// display form with title version number & input query data
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle(String.format("VSP Demo v%s", Vsp_Demo.getVersion()));
		updateCatalogUi(propertiesFileQuery);
		setVisible(true);
	}

	
	/**
	 * Configure listener for properties filewriter events 
	 * 
	 * @param writePropsListener reference to PropertiesFileWriter instance
	 */
	public void setPropsWriterListener(FileWriterListener writePropsListener) {
		this.writePropsListener = writePropsListener;
	}

	/**
	 * Configures listener for radewriter events
	 * 
	 *@param writeRaDecListener reference to RaDecFileWriter instance
	 */
	public void setRaDecWriterListener(FileWriterListener writeRaDecListener) {
		this.writeRaDecListener = writeRaDecListener;
	}
	
	/**
	 * Encapsulates current catalog form inputs in a CatalogQuery object
	 * 
	 * @return query encapsulating catalog data fields and combo selections
	 */
	public CatalogQuery compileQuery() {
		CatalogQuery query = new CatalogQuery();

		// text field data
		query.setObjectId(objectIdField.getText());
		query.setRaHr(AstroCoords.raHms_To_raHr(raField.getText()));
		query.setDecDeg(AstroCoords.decDms_To_decDeg(decField.getText()));
		query.setFovAmin(Double.parseDouble(fovField.getText()));
		query.setMagLimit(Double.parseDouble(magLimitField.getText()));

		// combo selections
		query.setCatalogType(CatalogType.valueOf(catalogCombo.getSelectedItem().toString()));
		query.setMagBand(filterCombo.getSelectedItem().toString());
		return query;
	}

	
	// Copies query data to textfield and combo controls
	private void updateCatalogUi(CatalogQuery query) {
		objectIdField.setText(query.getObjectId());
		raField.setText(AstroCoords.raHr_To_raHms(query.getRaHr()));
		decField.setText(AstroCoords.decDeg_To_decDms(query.getDecDeg()));
		fovField.setText(String.format("%.1f", query.getFovAmin()));
		magLimitField.setText(String.format("%.1f", query.getMagLimit()));
		
		// populate filter combo with catalog set & select current filter
		String selectedCatalog = query.getCatalogType().toString().toUpperCase();
		catalogCombo.setSelectedItem(selectedCatalog);
		populateFilterCombo(selectedCatalog, query.getMagBand());
	}
	

	/*
	 * Configures text field input verifiers and button event listeners
	 */
	private void setupActionListeners() {
		// validation of jtextfield values runs when user presses <Enter>
		// alphanumeric objectId field
		objectIdField.addActionListener(e -> verifyTextField(objectIdField));

		// sexagesimal ra & dec fields
		raField.addActionListener(e -> verifyTextField(raField));
		decField.addActionListener(e -> verifyTextField(decField));

		// numeric fov & maglimit fields
		fovField.addActionListener(e -> verifyTextField(fovField));
		magLimitField.addActionListener(e -> verifyTextField(magLimitField));

		// handles change in selected catalog (VSP, APASS ..)
		catalogCombo.addItemListener(ie -> selectCatalog(ie));

		// SIMBAD button => check if valid simbad object id
		simbadButton.addActionListener(e -> runSimbadQuery());

		// Catalog button => query selected catalog (VSP, APASS..)
		catalogButton.addActionListener(e -> saveRaDecFile());

		// Save button => save properties file + inform user
		saveButton.addActionListener(e -> savePropertiesFile());

		// close => lose unsaved changes
		cancelButton.addActionListener(e -> System.exit(0));
	}

	/**
	 * Handles Simbad query request, if objectid found then update ra and dec values,
	 * otherwise handle SimbadNotFound exception
	 */
	private void runSimbadQuery() {
		CatalogQuery query = compileQuery();
		try {
			updateCatalogUi(simbad.runQuery(query));
		} catch (SimbadNotFoundException se) {
			updateCatalogUi(query);
			JOptionPane.showMessageDialog(null, se.getMessage(), TITLE, JOptionPane.INFORMATION_MESSAGE);
		}
	}

	// handles (vsp) catalog query request, if good then write radec and image fits files
	private void saveRaDecFile() {
		String message = INVALID_DATA;
		// check all text field values are valid
		if (verifyAllInputs()) {
			CatalogQuery query = compileQuery();
			message = writeRaDecListener.writeFile(query);
			// if message is not empty then show in message dialog
			if (message.length() > 0) {
				JOptionPane.showMessageDialog(null, message, TITLE, JOptionPane.INFORMATION_MESSAGE);
			}
		} else {
			// at least one invalid data entry
			JOptionPane.showMessageDialog(null, message, TITLE, JOptionPane.INFORMATION_MESSAGE);
		}
	}

	// handles properties file request
	private void savePropertiesFile() {
		String message = INVALID_DATA;
		if (verifyAllInputs()) {
			message = writePropsListener.writeFile(compileQuery());
			JOptionPane.showMessageDialog(null, message, TITLE, JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(null, message, TITLE, JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Update ra, dec and label fields if valid simbad objectid query
	 * invalid simbad id then inform user & set labels = "." (no data)
	 * 
	 * @param result if query is good, then Simbad ra and dec values, otherwise null
	 */
	private void updateCatalogUi(SimbadResult result) {
		if (result == null) {
			simbadIdLabel.setText(".");
			simbadRaLabel.setText(".");
			simbadDecLabel.setText(".");
			simbadMagBLabel.setText(".");
			simbadMagVLabel.setText(".");
			simbadMagRLabel.setText(".");
			simbadMagILabel.setText(".");
		} else {
			// convert numeric coors to sexagesimal format
			String raHms = AstroCoords.raHr_To_raHms(result.getSimbadRaHr());
			String decDms = AstroCoords.decDeg_To_decDms(result.getSimbadDecDeg());

			// update catalog ra, dec values in black
			raField.setText(raHms);
			decField.setText(decDms);
			raField.setForeground(Color.black);
			decField.setForeground(Color.black);

			// update info labels
			simbadIdLabel.setText(result.getSimbadId());
			simbadRaLabel.setText(raHms);
			simbadDecLabel.setText(decDms);

			// handle no data, usually R and I bands
			String mag = (result.getMagB() == null) ? "." : result.getMagB().toString();
			simbadMagBLabel.setText(mag);

			mag = (result.getMagV() == null) ? "." : result.getMagV().toString();
			simbadMagVLabel.setText(mag);

			mag = (result.getMagR() == null) ? "." : result.getMagR().toString();
			simbadMagRLabel.setText(mag);

			mag = (result.getMagI() == null) ? "." : result.getMagI().toString();
			simbadMagILabel.setText(mag);
		}
	}

		
	/*
	 * Handles change in catalogCombo selection. 
	 * Clears existing filterCombo list and loads a new list based on CatalogType enum
	 * 
	 * @param ie event indicates an item was selected in the catalogCombo control 
	 */
	private void selectCatalog(ItemEvent ie) {
		if (ie.getStateChange() == ItemEvent.SELECTED) {
			// get current catalogCombo selection & catalog type
			String selectedCatalog = catalogCombo.getSelectedItem().toString().toUpperCase();
			CatalogType catalog = CatalogType.valueOf(selectedCatalog);

			// populate filterCombo and select first item 
			populateFilterCombo(selectedCatalog, catalog.getMagBands().get(0));
		}
	}

	
	/*
	 * Clears current and imports new filter list in the filter selection combo
	 *  
	 * @param selectedCatalog uppercase name of current catalog selected in catalog combo
	 * 
	 * @param selectedFilter filter name of current filter selection in filter combo
	 */
	private void populateFilterCombo(String selectedCatalog, String selectedFilter) {
		// clear filters list
		filterCombo.removeAllItems();
		
		// retrieve catalog from enum
		CatalogType catalog = CatalogType.valueOf(selectedCatalog);
		
		// import filter list for selected catalog & select specified filter
		catalog.getMagBands().forEach(item -> filterCombo.addItem(item));
		filterCombo.setSelectedItem(selectedFilter);
	}

	 // Validates user input for active text field
	private boolean verifyTextField(JTextField textField) {
		// defaults to invalid
		textField.setForeground(Color.red);

		// Get input and active text field, identified by value of TextFieldType enum
		String input = textField.getText().trim();
		TextFieldType formField = TextFieldType.valueOfFieldName(textField.getName());
		
		// verify input for current text field
		boolean isValid = inputVerifier.verifyInput(input, formField);

		// valid input => update text and set focus to the next text field
		if (isValid) {
			textField.setText(AstroCoords.sexagesimalFormatter(input, formField));
			textField.setForeground(Color.black);
			setFocus(textField);
		}
		return isValid;
	}

	// Confirms that all text fields are valid before submitting on-line search
	private boolean verifyAllInputs() {
		boolean isValid = true;

		for (JTextField textField : textFields) {
			String input = textField.getText().trim();
			TextFieldType dataType = TextFieldType.valueOfFieldName(textField.getName());
			isValid = isValid && inputVerifier.verifyInput(input, dataType);
		}
		return isValid;
	}

	// Moves focus to next text field in sequence
	private void setFocus(JTextField textField) {
		for (JTextField field : textFields) {
			if (field.equals(textField)) {
				// cyclic list to find nextField to request focus
				int idx = (textFields.indexOf(field) + 1) % textFields.size();
				textFields.get(idx).requestFocus();
				break;
			}
		}
	}

	// Sets up form text fields for data validation and focus order
	private void configureTextFields() {		
		// Maps TextFieldType enum to text field to identify active field
		objectIdField.setName(TextFieldType.OBJECT_ID.getFieldName());
		raField.setName(TextFieldType.RA_HMS.getFieldName());
		decField.setName(TextFieldType.DEC_DMS.getFieldName());
		fovField.setName(TextFieldType.FOV_AMIN.getFieldName());
		magLimitField.setName(TextFieldType.MAG_LIMIT.getFieldName());

		// compile arraylist JTextFields in circular focus set order
		JTextField[] arr = { objectIdField, raField, decField, fovField, magLimitField };
		textFields = Arrays.asList(arr);
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
		ResourceBundle bundle = ResourceBundle.getBundle("VspDataUI");
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		panel1 = new JPanel();
		label1 = new JLabel();
		objectIdField = new JTextField();
		label2 = new JLabel();
		raField = new JTextField();
		label3 = new JLabel();
		decField = new JTextField();
		label4 = new JLabel();
		fovField = new JTextField();
		label5 = new JLabel();
		magLimitField = new JTextField();
		label7 = new JLabel();
		label8 = new JLabel();
		label9 = new JLabel();
		label10 = new JLabel();
		catalogCombo = new JComboBox<>();
		label11 = new JLabel();
		filterCombo = new JComboBox<>();
		label12 = new JLabel();
		panel2 = new JPanel();
		simbadButton = new JButton();
		cancelButton = new JButton();
		catalogButton = new JButton();
		saveButton = new JButton();
		panel3 = new JPanel();
		idLabel = new JLabel();
		raLabel = new JLabel();
		decLabel = new JLabel();
		simbadIdLabel = new JLabel();
		simbadRaLabel = new JLabel();
		simbadDecLabel = new JLabel();
		panel4 = new JPanel();
		idLabel2 = new JLabel();
		idLabel3 = new JLabel();
		idLabel4 = new JLabel();
		idLabel5 = new JLabel();
		simbadMagBLabel = new JLabel();
		simbadMagVLabel = new JLabel();
		simbadMagRLabel = new JLabel();
		simbadMagILabel = new JLabel();

		// ======== this ========
		setTitle("VSP Demo v0.1");
		setResizable(false);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		// ======== dialogPane ========
		{
			dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
			dialogPane.setLayout(new BorderLayout());

			// ======== contentPanel ========
			{

				// ======== panel1 ========
				{

					// ---- label1 ----
					label1.setText(bundle.getString("VspDataUI.label1.text_3"));

					// ---- objectIdField ----
					objectIdField.setText(bundle.getString("VspDataUI.objectIdField.text"));
					objectIdField.setFocusCycleRoot(true);
					objectIdField.setBackground(Color.white);
					objectIdField.setToolTipText(bundle.getString("VspDataUI.objectIdField.toolTipText"));

					// ---- label2 ----
					label2.setText(bundle.getString("VspDataUI.label2.text_3"));

					// ---- raField ----
					raField.setText("06:30:32.80");
					raField.setToolTipText(bundle.getString("VspDataUI.raField.toolTipText"));

					// ---- label3 ----
					label3.setText(bundle.getString("VspDataUI.label3.text_3"));

					// ---- decField ----
					decField.setText("+20:40:20.27");
					decField.setToolTipText(bundle.getString("VspDataUI.decField.toolTipText"));

					// ---- label4 ----
					label4.setText(bundle.getString("VspDataUI.label4.text_3"));

					// ---- fovField ----
					fovField.setText("60.0");
					fovField.setToolTipText(bundle.getString("VspDataUI.fovField.toolTipText"));

					// ---- label5 ----
					label5.setText(bundle.getString("VspDataUI.label5.text_3"));

					// ---- magLimitField ----
					magLimitField.setText(bundle.getString("VspDataUI.magLimitField.text"));
					magLimitField.setToolTipText(bundle.getString("VspDataUI.magLimitField.toolTipText"));

					// ---- label7 ----
					label7.setText(bundle.getString("VspDataUI.label7.text_2"));

					// ---- label8 ----
					label8.setText(bundle.getString("VspDataUI.label8.text_2"));

					// ---- label9 ----
					label9.setText(bundle.getString("VspDataUI.label9.text_2"));

					// ---- label10 ----
					label10.setText(bundle.getString("VspDataUI.label10.text_2"));

					// ---- catalogCombo ----
					catalogCombo.setModel(new DefaultComboBoxModel<>(new String[] { "VSP", "APASS" }));

					// ---- label11 ----
					label11.setText(bundle.getString("VspDataUI.label11.text"));

					// ---- label12 ----
					label12.setText(bundle.getString("VspDataUI.label12.text"));

					GroupLayout panel1Layout = new GroupLayout(panel1);
					panel1.setLayout(panel1Layout);
					panel1Layout.setHorizontalGroup(panel1Layout.createParallelGroup()
							.addGroup(panel1Layout.createSequentialGroup().addGap(15, 15, 15)
									.addGroup(panel1Layout.createParallelGroup()
											.addComponent(label1, GroupLayout.Alignment.TRAILING)
											.addComponent(label2, GroupLayout.Alignment.TRAILING)
											.addComponent(label3, GroupLayout.Alignment.TRAILING)
											.addComponent(label4, GroupLayout.Alignment.TRAILING)
											.addComponent(label5, GroupLayout.Alignment.TRAILING)
											.addComponent(label11, GroupLayout.Alignment.TRAILING)
											.addComponent(label12, GroupLayout.Alignment.TRAILING))
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addGroup(panel1Layout.createParallelGroup()
											.addComponent(filterCombo, GroupLayout.PREFERRED_SIZE,
													GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addGroup(panel1Layout
													.createParallelGroup(GroupLayout.Alignment.LEADING, false)
													.addGroup(panel1Layout.createSequentialGroup().addGroup(panel1Layout
															.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
															.addComponent(fovField, GroupLayout.Alignment.LEADING)
															.addComponent(decField, GroupLayout.Alignment.LEADING,
																	GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
															.addComponent(raField, GroupLayout.Alignment.LEADING)
															.addComponent(magLimitField))
															.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
															.addGroup(panel1Layout.createParallelGroup()
																	.addComponent(label7).addComponent(label8)
																	.addComponent(label9).addComponent(label10)))
													.addComponent(objectIdField).addComponent(catalogCombo,
															GroupLayout.PREFERRED_SIZE, 96,
															GroupLayout.PREFERRED_SIZE)))
									.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
					panel1Layout.setVerticalGroup(panel1Layout.createParallelGroup().addGroup(panel1Layout
							.createSequentialGroup().addContainerGap()
							.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(label1).addComponent(objectIdField, GroupLayout.PREFERRED_SIZE,
											GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGroup(panel1Layout.createParallelGroup()
									.addGroup(panel1Layout.createSequentialGroup().addGap(16, 16, 16)
											.addComponent(label2))
									.addGroup(panel1Layout.createSequentialGroup()
											.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
											.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
													.addComponent(raField, GroupLayout.PREFERRED_SIZE,
															GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
													.addComponent(label7))))
							.addGroup(panel1Layout.createParallelGroup()
									.addGroup(panel1Layout.createSequentialGroup().addGap(16, 16, 16)
											.addComponent(label3))
									.addGroup(panel1Layout.createSequentialGroup()
											.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
											.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
													.addComponent(decField, GroupLayout.PREFERRED_SIZE,
															GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
													.addComponent(label8))))
							.addGroup(panel1Layout.createParallelGroup()
									.addGroup(panel1Layout.createSequentialGroup().addGap(16, 16, 16)
											.addComponent(label4))
									.addGroup(panel1Layout.createSequentialGroup()
											.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
											.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
													.addComponent(fovField, GroupLayout.PREFERRED_SIZE,
															GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
													.addComponent(label9))))
							.addGroup(panel1Layout.createParallelGroup()
									.addGroup(panel1Layout.createSequentialGroup().addGap(16, 16, 16)
											.addComponent(label5))
									.addGroup(panel1Layout.createSequentialGroup()
											.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
											.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
													.addComponent(magLimitField, GroupLayout.PREFERRED_SIZE,
															GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
													.addComponent(label10))))
							.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
							.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(catalogCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
											GroupLayout.PREFERRED_SIZE)
									.addComponent(label11))
							.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
							.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(filterCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
											GroupLayout.PREFERRED_SIZE)
									.addComponent(label12))
							.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
				}

				// ======== panel2 ========
				{

					// ---- simbadButton ----
					simbadButton.setText(bundle.getString("VspDataUI.simbadButton.text"));
					simbadButton.setToolTipText(bundle.getString("VspDataUI.simbadButton.toolTipText"));

					// ---- cancelButton ----
					cancelButton.setText(bundle.getString("VspDataUI.cancelButton.text"));

					// ---- catalogButton ----
					catalogButton.setText("Catalog");
					catalogButton.setToolTipText(bundle.getString("VspDataUI.catalogButton.toolTipText"));

					// ---- saveButton ----
					saveButton.setText("Save");
					saveButton.setToolTipText(bundle.getString("VspDataUI.saveButton.toolTipText"));

					GroupLayout panel2Layout = new GroupLayout(panel2);
					panel2.setLayout(panel2Layout);
					panel2Layout.setHorizontalGroup(panel2Layout.createParallelGroup()
							.addGroup(panel2Layout.createSequentialGroup().addContainerGap()
									.addGroup(panel2Layout.createParallelGroup()
											.addComponent(saveButton, GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
											.addComponent(catalogButton, GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
											.addComponent(simbadButton, GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
											.addComponent(cancelButton, GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE))
									.addContainerGap()));
					panel2Layout
							.setVerticalGroup(panel2Layout.createParallelGroup()
									.addGroup(panel2Layout.createSequentialGroup().addContainerGap()
											.addComponent(simbadButton)
											.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
											.addComponent(catalogButton)
											.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
											.addComponent(saveButton)
											.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
													GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
											.addComponent(cancelButton).addContainerGap()));
				}

				// ======== panel3 ========
				{
					panel3.setBorder(new TitledBorder(bundle.getString("VspDataUI.panel3.border")));
					panel3.setPreferredSize(new Dimension(190, 164));
					panel3.setToolTipText(bundle.getString("VspDataUI.panel3.toolTipText"));

					// ---- idLabel ----
					idLabel.setText(bundle.getString("VspDataUI.idLabel.text_2"));

					// ---- raLabel ----
					raLabel.setText(bundle.getString("VspDataUI.raLabel.text"));

					// ---- decLabel ----
					decLabel.setText(bundle.getString("VspDataUI.decLabel.text"));

					// ---- simbadIdLabel ----
					simbadIdLabel.setText(".");

					// ---- simbadRaLabel ----
					simbadRaLabel.setText("HH:MM:SS.SS");

					// ---- simbadDecLabel ----
					simbadDecLabel.setText("DD:MM:SS.SS");

					GroupLayout panel3Layout = new GroupLayout(panel3);
					panel3.setLayout(panel3Layout);
					panel3Layout.setHorizontalGroup(panel3Layout.createParallelGroup()
							.addGroup(panel3Layout.createSequentialGroup()
									.addGroup(panel3Layout.createParallelGroup()
											.addGroup(panel3Layout.createSequentialGroup().addGap(25, 25, 25)
													.addGroup(panel3Layout.createParallelGroup()
															.addComponent(raLabel, GroupLayout.Alignment.TRAILING)
															.addComponent(decLabel, GroupLayout.Alignment.TRAILING)))
											.addGroup(panel3Layout.createSequentialGroup().addContainerGap()
													.addComponent(idLabel)))
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addGroup(panel3Layout.createParallelGroup().addComponent(simbadIdLabel)
											.addComponent(simbadRaLabel).addComponent(simbadDecLabel))
									.addGap(0, 106, Short.MAX_VALUE)));
					panel3Layout.setVerticalGroup(panel3Layout.createParallelGroup()
							.addGroup(panel3Layout.createSequentialGroup().addContainerGap()
									.addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
											.addComponent(simbadIdLabel).addComponent(idLabel))
									.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
									.addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
											.addComponent(raLabel).addComponent(simbadRaLabel))
									.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
									.addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
											.addComponent(decLabel).addComponent(simbadDecLabel))
									.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
				}

				// ======== panel4 ========
				{
					panel4.setBorder(new TitledBorder(bundle.getString("VspDataUI.panel4.border")));
					panel4.setPreferredSize(new Dimension(190, 164));
					panel4.setToolTipText(bundle.getString("VspDataUI.panel4.toolTipText"));

					// ---- idLabel2 ----
					idLabel2.setText(bundle.getString("VspDataUI.idLabel2.text"));

					// ---- idLabel3 ----
					idLabel3.setText(bundle.getString("VspDataUI.idLabel3.text"));

					// ---- idLabel4 ----
					idLabel4.setText(bundle.getString("VspDataUI.idLabel4.text"));

					// ---- idLabel5 ----
					idLabel5.setText(bundle.getString("VspDataUI.idLabel5.text"));

					// ---- simbadMagBLabel ----
					simbadMagBLabel.setText(".");

					// ---- simbadMagVLabel ----
					simbadMagVLabel.setText(".");

					// ---- simbadMagRLabel ----
					simbadMagRLabel.setText(".");

					// ---- simbadMagILabel ----
					simbadMagILabel.setText(".");

					GroupLayout panel4Layout = new GroupLayout(panel4);
					panel4.setLayout(panel4Layout);
					panel4Layout.setHorizontalGroup(panel4Layout.createParallelGroup()
							.addGroup(panel4Layout.createSequentialGroup().addContainerGap()
									.addGroup(panel4Layout.createParallelGroup()
											.addComponent(idLabel2, GroupLayout.Alignment.TRAILING)
											.addComponent(idLabel3, GroupLayout.Alignment.TRAILING)
											.addComponent(idLabel4, GroupLayout.Alignment.TRAILING)
											.addComponent(idLabel5, GroupLayout.Alignment.TRAILING))
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addGroup(panel4Layout.createParallelGroup().addComponent(simbadMagBLabel)
											.addComponent(simbadMagVLabel).addComponent(simbadMagRLabel)
											.addComponent(simbadMagILabel))
									.addContainerGap(44, Short.MAX_VALUE)));
					panel4Layout.setVerticalGroup(panel4Layout.createParallelGroup()
							.addGroup(panel4Layout.createSequentialGroup().addContainerGap()
									.addGroup(panel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
											.addComponent(idLabel2).addComponent(simbadMagBLabel))
									.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
									.addGroup(panel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
											.addComponent(idLabel3).addComponent(simbadMagVLabel))
									.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
									.addGroup(panel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
											.addComponent(idLabel4).addComponent(simbadMagRLabel))
									.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
									.addGroup(panel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
											.addComponent(idLabel5).addComponent(simbadMagILabel))
									.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
				}

				GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
				contentPanel.setLayout(contentPanelLayout);
				contentPanelLayout.setHorizontalGroup(contentPanelLayout.createParallelGroup()
						.addGroup(GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
								.addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
										.addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addComponent(panel3, GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE))
								.addGap(18, 18, 18)
								.addGroup(contentPanelLayout.createParallelGroup()
										.addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
												GroupLayout.PREFERRED_SIZE)
										.addGroup(
												contentPanelLayout.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE)
														.addComponent(panel4, GroupLayout.PREFERRED_SIZE, 116,
																GroupLayout.PREFERRED_SIZE)))));
				contentPanelLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] { panel2, panel4 });
				contentPanelLayout.setVerticalGroup(contentPanelLayout.createParallelGroup().addGroup(contentPanelLayout
						.createSequentialGroup()
						.addGroup(contentPanelLayout.createParallelGroup()
								.addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(panel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(contentPanelLayout.createParallelGroup()
								.addComponent(panel4, GroupLayout.PREFERRED_SIZE, 151, GroupLayout.PREFERRED_SIZE)
								.addComponent(panel3, GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE))
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JPanel panel1;
	private JLabel label1;
	private JTextField objectIdField;
	private JLabel label2;
	private JTextField raField;
	private JLabel label3;
	private JTextField decField;
	private JLabel label4;
	private JTextField fovField;
	private JLabel label5;
	private JTextField magLimitField;
	private JLabel label7;
	private JLabel label8;
	private JLabel label9;
	private JLabel label10;
	private JComboBox<String> catalogCombo;
	private JLabel label11;
	private JComboBox<String> filterCombo;
	private JLabel label12;
	private JPanel panel2;
	private JButton simbadButton;
	private JButton cancelButton;
	private JButton catalogButton;
	private JButton saveButton;
	private JPanel panel3;
	private JLabel idLabel;
	private JLabel raLabel;
	private JLabel decLabel;
	private JLabel simbadIdLabel;
	private JLabel simbadRaLabel;
	private JLabel simbadDecLabel;
	private JPanel panel4;
	private JLabel idLabel2;
	private JLabel idLabel3;
	private JLabel idLabel4;
	private JLabel idLabel5;
	private JLabel simbadMagBLabel;
	private JLabel simbadMagVLabel;
	private JLabel simbadMagRLabel;
	private JLabel simbadMagILabel;
	// JFormDesigner - End of variables declaration //GEN-END:variables

}
