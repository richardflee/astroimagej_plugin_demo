/*
 * Created by JFormDesigner on Wed Apr 07 08:13:13 BST 2021
 */

package rfl.astroimagej.dev.catalog_ui;

import java.awt.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
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
import javax.swing.UIManager;
import javax.swing.border.*;
import javax.swing.border.EmptyBorder;

import rfl.astroimagej.dev.catalogs.AstroCatalog;
import rfl.astroimagej.dev.catalogs.CatalogFactory;
import rfl.astroimagej.dev.catalogs.SimbadCatalog;
import rfl.astroimagej.dev.enums.CatalogMagType;
import rfl.astroimagej.dev.properties.PropertiesReadWriter;
import rfl.astroimagej.dev.queries.CatalogQuery;
import rfl.astroimagej.dev.queries.QueryResult;
import rfl.astroimagej.dev.utils.AstroCoords;
import rfl.astroimagej.exceptions.SimbadNotFoundException;

/**
 * Form for user-specified inputs to download astronomical photometry catalog
 * data
 * 
 * <p>
 * Current implementation is specific to the AAVSO VSP catalog (2021-04-08)
 * </p>
 */
public class CatalogFormUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private PropertiesReadWriter propReadWriter;
	private CatalogFormVerifier inputVerifier;
	private List<JTextField> textFields;
	private SimbadCatalog simbad;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			System.err.println("Failed to initialize Windows Look-Feel");
		}

		EventQueue.invokeLater(() -> {
			new CatalogFormUI(new PropertiesReadWriter());
		});
	}

	/**
	 * Opens form to enter VSP target name and related data. User clicks Download to
	 * run on-line query.
	 * 
	 * @param propReadWriter object methods to load or save text field values to
	 *                       dspdemo.properties file
	 * 
	 */
	public CatalogFormUI(PropertiesReadWriter propReadWriter) {
		// form generated in jformdesigner gui builder (GroupLayout)
		initComponents();

		// set object names
		initFieldNames();

		// compile arraylist JTextFields in circular focus set order
		JTextField[] arr = { objectIdField, raField, decField, fovField, magLimitField };
		textFields = Arrays.asList(arr);

		// methods to import and save properties file data
		this.propReadWriter = propReadWriter;
		importPropertiesData(propReadWriter);

		// verifier for user entry in chart_ui text fields
		inputVerifier = new CatalogFormVerifier();

		// setup text field and button action listeners
		setupActionListeners();

		simbad = new SimbadCatalog();

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	/*
	 * Reads property file and sets data to chart_ui jtextfields & combos
	 */
	private void importPropertiesData(PropertiesReadWriter propReadWriter) {
		// read property file into FormData obj
		CatalogQuery cq = propReadWriter.readVspProperties();

		// copy FormData values to JTextFields
		objectIdField.setText(cq.getObjectId());
		raField.setText(AstroCoords.raHr_To_raHms(cq.getRaHr()));
		decField.setText(AstroCoords.decDeg_To_decDms(cq.getDecDeg()));

		fovField.setText(String.format("%.1f", cq.getFovAmin()));
		magLimitField.setText(String.format("%.1f", cq.getMagLimit()));

		catalogCombo.setSelectedItem(cq.getCatalogType().toString());
		populateFilterCombo(cq.getCatalogType(), cq.getMagBand());
	}

	/*
	 * Save jtextfield data to property file
	 * 
	 * @return FormData object with text field values
	 */
	private CatalogQuery writePropertiesData() {
		CatalogQuery data = new CatalogQuery();

		// copy text field data to FieldData obj

		data.setCatalogType(CatalogMagType.valueOf(catalogCombo.getSelectedItem().toString()));

		data.setObjectId(objectIdField.getText());

		data.setRaHr(AstroCoords.raHms_To_raHr(raField.getText()));

		data.setDecDeg(AstroCoords.decDms_To_decDeg(decField.getText()));

		data.setFovAmin(Double.parseDouble(fovField.getText()));

		data.setMagLimit(Double.parseDouble(magLimitField.getText()));

		data.setMagBand(filterCombo.getSelectedItem().toString());

		// save text field data to properties file
		propReadWriter.writeVspProperties(data);
		return data;
	}

	/*
	 * Configure download button to run on-line catalog search & setup text field
	 * input validation
	 */
	private void setupActionListeners() {
		// validation of jtextfield values runs when user presses <Enter>
		objectIdField.addActionListener(e -> verifyTextField(objectIdField));
		raField.addActionListener(e -> verifyTextField(raField));
		decField.addActionListener(e -> verifyTextField(decField));
		fovField.addActionListener(e -> verifyTextField(fovField));
		magLimitField.addActionListener(e -> verifyTextField(magLimitField));

		// handles change in selected catalog (VSP, APASS ..)
		catalogCombo.addItemListener(ie -> selectCatalog(ie));

		// close
		cancelButton.addActionListener(e -> System.exit(0));

		// user selects SIMBAD => check if valid simbad object id
		// write current form data to properties file
		// return catalog query object & run query
		simbadButton.addActionListener(e -> {
			CatalogQuery query = writePropertiesData();
			runSimbadQuery(query);
		});

		radecButton.addActionListener(e -> {
			if (verifyAllInputs()) {
				CatalogQuery query = writePropertiesData();
				AstroCatalog catalog = CatalogFactory.createCatalog(query.getCatalogType());
				QueryResult result = null;
				try {
					result = catalog.runQuery(query);
				} catch (SimbadNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.out.println(result.toString());
			} else {
				String msg = "One or more data entries are not in a valid format";
				JOptionPane.showMessageDialog(null, msg, 
						"SIMBAD Query Error", JOptionPane.INFORMATION_MESSAGE);
			}
		});
	}

	// valid SimbadId then update info labels & object ra & dec fields
	// otherwise ra/dec unchanged & '.' => no label data
	private void runSimbadQuery(CatalogQuery query) {
		// factory returns a simbad catalog object
		AstroCatalog catalog = CatalogFactory.createCatalog(CatalogMagType.SIMBAD);
		try {
			// run simbad query and pass to catalog update
			// updates catalog ra, dec and writes simbad data to tables
			updateCatalogUi(catalog.runQuery(query));
		} catch (SimbadNotFoundException se) {
			// ra, dec fields unchanged
			JOptionPane.showMessageDialog(null, se.getMessage(), 
					"SIMBAD Query Error", JOptionPane.INFORMATION_MESSAGE);
			updateCatalogUi(null);
		}
	}

	// update ra, dec and label fields if valid simbad objectid query
	// invalid simbad id then inform user & set labels = "." (no data)
	private void updateCatalogUi(QueryResult result) {
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

			// update catalog ra, dec
			raField.setText(raHms);
			decField.setText(decDms);

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
	 * Handles change in catalogCombo selection. Clears existing filterCombo list
	 * and loads new list. Default selection = item 0
	 */
	private void selectCatalog(ItemEvent ie) {
		if (ie.getStateChange() == ItemEvent.SELECTED) {
			// get current catalogCombo selection & associated AstroCatalog enum
			// populate filterCombo and select first item (index = 0)
			String selectedCatalog = catalogCombo.getSelectedItem().toString().toUpperCase();
			CatalogMagType catalog = CatalogMagType.valueOf(selectedCatalog);

			// select the first listed filter
			String selectFirst = catalog.getMagBand().substring(0, 1);
			populateFilterCombo(catalog, selectFirst);
		}
	}

	/*
	 * clear filterCombo, load list of filters in form <F1.F2.F3 ..> split filter
	 * list string on '.' and add to filterCombo items list selected item index
	 * number
	 */
	private void populateFilterCombo(CatalogMagType catalog, String selectedFilter) {
		filterCombo.removeAllItems();
		List<String> items = Arrays.asList(catalog.getMagBand().split("\\."));
		for (String item : items) {
			filterCombo.addItem(item);
		}
		filterCombo.setSelectedItem(selectedFilter);
	}

	/*
	 * Validates user input for active text field
	 */
	private boolean verifyTextField(JTextField textField) {
		// initialise text to red => indicates invalid data unless data is valid
		textField.setForeground(Color.red);

		// verify input for current jtextfield, identified by object name
		String input = textField.getText().trim();
		boolean isValid = inputVerifier.verifyInput(input, textField.getName().toLowerCase());

		// valid input => write data, minus any leading / trailing white space
		// & reset text colour to 'standard' black
		if (isValid) {
			textField.setText(input);
			textField.setForeground(Color.black);
			setFocus(textField);
		}
		return isValid;
	}

	/*
	 * confirm all text fields are valid before submitting on-line search
	 */
	private boolean verifyAllInputs() {
		boolean isValid = true;

		for (JTextField textField : textFields) {
			String input = textField.getText().trim();
			isValid = isValid && inputVerifier.verifyInput(input, textField.getName().toLowerCase());
		}
		return isValid;
	}

	/*
	 * move focus to next text field in sequence
	 */
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

	/*
	 * Set names for jtextfield objects to identify active field in verify methods
	 */
	private void initFieldNames() {
		objectIdField.setName("targetField");
		raField.setName("raField");
		decField.setName("decField");
		fovField.setName("fovField");
		magLimitField.setName("magLimitField");
		filterCombo.setName("filterCombo");
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
		radecButton = new JButton();
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

		//======== this ========
		setTitle("VSP Demo v0.1");
		setResizable(false);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{

				//======== panel1 ========
				{

					//---- label1 ----
					label1.setText(bundle.getString("VspDataUI.label1.text_3"));

					//---- objectIdField ----
					objectIdField.setText(bundle.getString("VspDataUI.objectIdField.text"));
					objectIdField.setFocusCycleRoot(true);
					objectIdField.setBackground(Color.white);

					//---- label2 ----
					label2.setText(bundle.getString("VspDataUI.label2.text_3"));

					//---- raField ----
					raField.setText("06:30:32.80");

					//---- label3 ----
					label3.setText(bundle.getString("VspDataUI.label3.text_3"));

					//---- decField ----
					decField.setText("+20:40:20.27");

					//---- label4 ----
					label4.setText(bundle.getString("VspDataUI.label4.text_3"));

					//---- fovField ----
					fovField.setText("60.0");

					//---- label5 ----
					label5.setText(bundle.getString("VspDataUI.label5.text_3"));

					//---- magLimitField ----
					magLimitField.setText(bundle.getString("VspDataUI.magLimitField.text"));

					//---- label7 ----
					label7.setText(bundle.getString("VspDataUI.label7.text_2"));

					//---- label8 ----
					label8.setText(bundle.getString("VspDataUI.label8.text_2"));

					//---- label9 ----
					label9.setText(bundle.getString("VspDataUI.label9.text_2"));

					//---- label10 ----
					label10.setText(bundle.getString("VspDataUI.label10.text_2"));

					//---- catalogCombo ----
					catalogCombo.setModel(new DefaultComboBoxModel<>(new String[] {
						"VSP",
						"APASS"
					}));

					//---- label11 ----
					label11.setText(bundle.getString("VspDataUI.label11.text"));

					//---- label12 ----
					label12.setText(bundle.getString("VspDataUI.label12.text"));

					GroupLayout panel1Layout = new GroupLayout(panel1);
					panel1.setLayout(panel1Layout);
					panel1Layout.setHorizontalGroup(
						panel1Layout.createParallelGroup()
							.addGroup(panel1Layout.createSequentialGroup()
								.addGap(15, 15, 15)
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
									.addComponent(filterCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
										.addGroup(panel1Layout.createSequentialGroup()
											.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
												.addComponent(fovField, GroupLayout.Alignment.LEADING)
												.addComponent(decField, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
												.addComponent(raField, GroupLayout.Alignment.LEADING)
												.addComponent(magLimitField))
											.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
											.addGroup(panel1Layout.createParallelGroup()
												.addComponent(label7)
												.addComponent(label8)
												.addComponent(label9)
												.addComponent(label10)))
										.addComponent(objectIdField)
										.addComponent(catalogCombo, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE)))
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					);
					panel1Layout.setVerticalGroup(
						panel1Layout.createParallelGroup()
							.addGroup(panel1Layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(label1)
									.addComponent(objectIdField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(panel1Layout.createParallelGroup()
									.addGroup(panel1Layout.createSequentialGroup()
										.addGap(16, 16, 16)
										.addComponent(label2))
									.addGroup(panel1Layout.createSequentialGroup()
										.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
											.addComponent(raField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(label7))))
								.addGroup(panel1Layout.createParallelGroup()
									.addGroup(panel1Layout.createSequentialGroup()
										.addGap(16, 16, 16)
										.addComponent(label3))
									.addGroup(panel1Layout.createSequentialGroup()
										.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
											.addComponent(decField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(label8))))
								.addGroup(panel1Layout.createParallelGroup()
									.addGroup(panel1Layout.createSequentialGroup()
										.addGap(16, 16, 16)
										.addComponent(label4))
									.addGroup(panel1Layout.createSequentialGroup()
										.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
											.addComponent(fovField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(label9))))
								.addGroup(panel1Layout.createParallelGroup()
									.addGroup(panel1Layout.createSequentialGroup()
										.addGap(16, 16, 16)
										.addComponent(label5))
									.addGroup(panel1Layout.createSequentialGroup()
										.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
											.addComponent(magLimitField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(label10))))
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(catalogCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(label11))
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(filterCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(label12))
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					);
				}

				//======== panel2 ========
				{

					//---- simbadButton ----
					simbadButton.setText(bundle.getString("VspDataUI.simbadButton.text"));

					//---- cancelButton ----
					cancelButton.setText(bundle.getString("VspDataUI.cancelButton.text"));

					//---- radecButton ----
					radecButton.setText("Save radec");

					GroupLayout panel2Layout = new GroupLayout(panel2);
					panel2.setLayout(panel2Layout);
					panel2Layout.setHorizontalGroup(
						panel2Layout.createParallelGroup()
							.addGroup(panel2Layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(panel2Layout.createParallelGroup()
									.addGroup(panel2Layout.createSequentialGroup()
										.addGap(0, 0, Short.MAX_VALUE)
										.addComponent(simbadButton))
									.addGroup(panel2Layout.createSequentialGroup()
										.addGroup(panel2Layout.createParallelGroup()
											.addComponent(radecButton)
											.addComponent(cancelButton))
										.addGap(0, 0, Short.MAX_VALUE)))
								.addContainerGap())
					);
					panel2Layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {cancelButton, radecButton, simbadButton});
					panel2Layout.setVerticalGroup(
						panel2Layout.createParallelGroup()
							.addGroup(panel2Layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(simbadButton)
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(radecButton)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(cancelButton)
								.addContainerGap())
					);
				}

				//======== panel3 ========
				{
					panel3.setBorder(new TitledBorder(bundle.getString("VspDataUI.panel3.border")));
					panel3.setPreferredSize(new Dimension(190, 164));

					//---- idLabel ----
					idLabel.setText(bundle.getString("VspDataUI.idLabel.text_2"));

					//---- raLabel ----
					raLabel.setText(bundle.getString("VspDataUI.raLabel.text"));

					//---- decLabel ----
					decLabel.setText(bundle.getString("VspDataUI.decLabel.text"));

					//---- simbadIdLabel ----
					simbadIdLabel.setText(".");

					//---- simbadRaLabel ----
					simbadRaLabel.setText("HH:MM:SS.SS");

					//---- simbadDecLabel ----
					simbadDecLabel.setText(".");

					GroupLayout panel3Layout = new GroupLayout(panel3);
					panel3.setLayout(panel3Layout);
					panel3Layout.setHorizontalGroup(
						panel3Layout.createParallelGroup()
							.addGroup(panel3Layout.createSequentialGroup()
								.addGroup(panel3Layout.createParallelGroup()
									.addGroup(panel3Layout.createSequentialGroup()
										.addGap(25, 25, 25)
										.addGroup(panel3Layout.createParallelGroup()
											.addComponent(raLabel, GroupLayout.Alignment.TRAILING)
											.addComponent(decLabel, GroupLayout.Alignment.TRAILING)))
									.addGroup(panel3Layout.createSequentialGroup()
										.addContainerGap()
										.addComponent(idLabel)))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(panel3Layout.createParallelGroup()
									.addComponent(simbadIdLabel)
									.addComponent(simbadRaLabel)
									.addComponent(simbadDecLabel))
								.addGap(0, 24, Short.MAX_VALUE))
					);
					panel3Layout.setVerticalGroup(
						panel3Layout.createParallelGroup()
							.addGroup(panel3Layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(simbadIdLabel)
									.addComponent(idLabel))
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(raLabel)
									.addComponent(simbadRaLabel))
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(decLabel)
									.addComponent(simbadDecLabel))
								.addContainerGap(52, Short.MAX_VALUE))
					);
				}

				//======== panel4 ========
				{
					panel4.setBorder(new TitledBorder(bundle.getString("VspDataUI.panel4.border")));
					panel4.setPreferredSize(new Dimension(190, 164));

					//---- idLabel2 ----
					idLabel2.setText(bundle.getString("VspDataUI.idLabel2.text"));

					//---- idLabel3 ----
					idLabel3.setText(bundle.getString("VspDataUI.idLabel3.text"));

					//---- idLabel4 ----
					idLabel4.setText(bundle.getString("VspDataUI.idLabel4.text"));

					//---- idLabel5 ----
					idLabel5.setText(bundle.getString("VspDataUI.idLabel5.text"));

					//---- simbadMagBLabel ----
					simbadMagBLabel.setText(".");

					//---- simbadMagVLabel ----
					simbadMagVLabel.setText(".");

					//---- simbadMagRLabel ----
					simbadMagRLabel.setText(".");

					//---- simbadMagILabel ----
					simbadMagILabel.setText(".");

					GroupLayout panel4Layout = new GroupLayout(panel4);
					panel4.setLayout(panel4Layout);
					panel4Layout.setHorizontalGroup(
						panel4Layout.createParallelGroup()
							.addGroup(panel4Layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(panel4Layout.createParallelGroup()
									.addComponent(idLabel2, GroupLayout.Alignment.TRAILING)
									.addComponent(idLabel3, GroupLayout.Alignment.TRAILING)
									.addComponent(idLabel4, GroupLayout.Alignment.TRAILING)
									.addComponent(idLabel5, GroupLayout.Alignment.TRAILING))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(panel4Layout.createParallelGroup()
									.addComponent(simbadMagBLabel)
									.addComponent(simbadMagVLabel)
									.addComponent(simbadMagRLabel)
									.addComponent(simbadMagILabel))
								.addContainerGap(118, Short.MAX_VALUE))
					);
					panel4Layout.setVerticalGroup(
						panel4Layout.createParallelGroup()
							.addGroup(panel4Layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(panel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(idLabel2)
									.addComponent(simbadMagBLabel))
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(panel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(idLabel3)
									.addComponent(simbadMagVLabel))
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(panel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(idLabel4)
									.addComponent(simbadMagRLabel))
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(panel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(idLabel5)
									.addComponent(simbadMagILabel))
								.addContainerGap(23, Short.MAX_VALUE))
					);
				}

				GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
				contentPanel.setLayout(contentPanelLayout);
				contentPanelLayout.setHorizontalGroup(
					contentPanelLayout.createParallelGroup()
						.addGroup(contentPanelLayout.createSequentialGroup()
							.addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(contentPanelLayout.createSequentialGroup()
							.addComponent(panel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(panel4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				);
				contentPanelLayout.setVerticalGroup(
					contentPanelLayout.createParallelGroup()
						.addGroup(contentPanelLayout.createSequentialGroup()
							.addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
								.addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(panel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addGroup(contentPanelLayout.createParallelGroup()
								.addComponent(panel4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(panel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addGap(0, 0, 0))
				);
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
	private JButton radecButton;
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
