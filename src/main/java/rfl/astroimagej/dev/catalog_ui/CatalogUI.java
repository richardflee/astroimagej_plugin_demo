/*
 * Created by JFormDesigner on Wed Apr 07 08:13:13 BST 2021
 */

package rfl.astroimagej.dev.catalog_ui;

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
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import rfl.astroimagej.dev.catalog.CatalogFilters;
import rfl.astroimagej.dev.catalog.FormData;
import rfl.astroimagej.dev.utils.AstroCoords;

/**
 * Form for user-specified inputs to download astronomical photometry catalog
 * data
 * 
 * <p>
 * Current implementation is specific to the AAVSO VSP catalog (2021-04-08)
 * </p>
 */
public class CatalogUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private PropertiesReadWriter propReadWriter;
	private InputVerifier inputVerifier;
	private List<JTextField> textFields;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			System.err.println("Failed to initialize Windows Look-Feel");
		}

		EventQueue.invokeLater(() -> {
			new CatalogUI(new PropertiesReadWriter());
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
	public CatalogUI(PropertiesReadWriter propReadWriter) {
		// form generated in jformdesigner gui builder (GroupLayout)
		initComponents();

		// set object names
		initFieldNames();

		// compile arraylist JTextFields in circular focus set order
		JTextField[] arr = { targetField, raField, decField, fovField, magLimitField };
		textFields = Arrays.asList(arr);

		// methods to import and save properties file data
		this.propReadWriter = propReadWriter;
		importPropertiesData(propReadWriter);

		// verifier for user entry in chart_ui text fields
		inputVerifier = new InputVerifier();

		// setup text field and button action listeners
		setupActionListeners();

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	/*
	 * Reads property file and sets data to chart_ui jtextfields & combos
	 */
	private void importPropertiesData(PropertiesReadWriter propReadWriter) {
		// read property file into FormData obj
		FormData f = propReadWriter.readVspProperties();

		// copy FormData values to JTextFields
		targetField.setText(f.getTargetName());
		raField.setText(AstroCoords.raHr_To_raHms(f.getRaHr()));
		decField.setText(AstroCoords.decDeg_To_decDms(f.getDecDeg()));

		fovField.setText(String.format("%.1f", f.getFovAmin()));
		magLimitField.setText(String.format("%.1f",  f.getMagLimit()));
		
		catalogCombo.setSelectedItem(f.getAstroCatalog().toString());		
		populateFilterCombo(f.getAstroCatalog(), f.getFilterBand());
	}

	/*
	 * Save jtextfield data to property file
	 * 
	 * @return FormData object with text field values
	 */
	private FormData writePropertiesData() {
		FormData f = new FormData();

		// copy text field data to FieldData obj

		f.setAstroCatalog(CatalogFilters.valueOf(catalogCombo.getSelectedItem().toString()));

		f.setTargetName(targetField.getText());

		f.setRaHr(AstroCoords.raHms_To_raHr(raField.getText()));

		f.setDecDeg(AstroCoords.decDms_To_decDeg(decField.getText()));

		f.setFovAmin(Double.parseDouble(fovField.getText()));

		f.setMagLimit(Double.parseDouble(magLimitField.getText()));

		f.setFilterBand(filterCombo.getSelectedItem().toString());

		// save text field data to properties file
		propReadWriter.writeVspProperties(f);
		return f;
	}

	/*
	 * Configure download button to run on-line catalog search & setup text field
	 * input validation
	 */
	private void setupActionListeners() {
		// validation of jtextfield values runs when user presses <Enter>
		targetField.addActionListener(e -> verifyTextField(targetField));
		raField.addActionListener(e -> verifyTextField(raField));
		decField.addActionListener(e -> verifyTextField(decField));
		fovField.addActionListener(e -> verifyTextField(fovField));
		magLimitField.addActionListener(e -> verifyTextField(magLimitField));

		// handles change in selected catalog (VSP, APASS ..)
		catalogCombo.addItemListener(ie -> selectCatalog(ie));

		// close
		cancelButton.addActionListener(e -> System.exit(0));

		// user selects download => copy form data to a RequestEvent object
		// and trigger requestPerformed method in the request listener
		downLoadButton.addActionListener(e -> {
			if (verifyAllInputs()) {
				FormData fData = writePropertiesData();
			}
		});
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
			CatalogFilters catalog = CatalogFilters.valueOf(selectedCatalog);

			// select the first listed filter
			String selectFirst = catalog.getFilters().substring(0, 1);
			populateFilterCombo(catalog, selectFirst);
		}
	}

	/*
	 * clear filterCombo, load list of filters in form <F1.F2.F3 ..> split filter
	 * list string on '.' and add to filterCombo items list selected item index
	 * number
	 */
	private void populateFilterCombo(CatalogFilters catalog, String selectedFilter) {
		filterCombo.removeAllItems();
		List<String> items = Arrays.asList(catalog.getFilters().split("\\."));
		for (String item : items) {
			filterCombo.addItem(item);
		}
		filterCombo.setSelectedItem(selectedFilter);
	}

	/*
	 * Validates user input for active text field
	 */
	private void verifyTextField(JTextField textField) {
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
		targetField.setName("targetField");
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
		targetField = new JTextField();
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
		panel2 = new JPanel();
		downLoadButton = new JButton();
		cancelButton = new JButton();
		label6 = new JLabel();
		filterCombo = new JComboBox<>();

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

					// ---- targetField ----
					targetField.setText(bundle.getString("VspDataUI.targetField.text"));
					targetField.setFocusCycleRoot(true);

					// ---- label2 ----
					label2.setText(bundle.getString("VspDataUI.label2.text_3"));

					// ---- raField ----
					raField.setText("06:30:32.80");

					// ---- label3 ----
					label3.setText(bundle.getString("VspDataUI.label3.text_3"));

					// ---- decField ----
					decField.setText("+20:40:20.27");

					// ---- label4 ----
					label4.setText(bundle.getString("VspDataUI.label4.text_3"));

					// ---- fovField ----
					fovField.setText("60.0");

					// ---- label5 ----
					label5.setText(bundle.getString("VspDataUI.label5.text_3"));

					// ---- magLimitField ----
					magLimitField.setText(bundle.getString("VspDataUI.magLimitField.text"));

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

					GroupLayout panel1Layout = new GroupLayout(panel1);
					panel1.setLayout(panel1Layout);
					panel1Layout.setHorizontalGroup(panel1Layout.createParallelGroup()
							.addGroup(panel1Layout.createSequentialGroup().addGap(15, 15, 15)
									.addGroup(panel1Layout
											.createParallelGroup(GroupLayout.Alignment.TRAILING)
											.addGroup(panel1Layout
													.createSequentialGroup().addGroup(panel1Layout
															.createParallelGroup(GroupLayout.Alignment.TRAILING)
															.addComponent(
																	label1)
															.addComponent(label2).addComponent(label3)
															.addComponent(label4).addComponent(label5))
													.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
													.addGroup(panel1Layout
															.createParallelGroup(GroupLayout.Alignment.LEADING, false)
															.addGroup(panel1Layout.createSequentialGroup()
																	.addGroup(panel1Layout
																			.createParallelGroup(
																					GroupLayout.Alignment.TRAILING,
																					false)
																			.addComponent(fovField,
																					GroupLayout.Alignment.LEADING)
																			.addComponent(decField,
																					GroupLayout.Alignment.LEADING,
																					GroupLayout.DEFAULT_SIZE, 96,
																					Short.MAX_VALUE)
																			.addComponent(raField,
																					GroupLayout.Alignment.LEADING)
																			.addComponent(magLimitField))
																	.addPreferredGap(
																			LayoutStyle.ComponentPlacement.RELATED)
																	.addGroup(panel1Layout.createParallelGroup()
																			.addComponent(label7).addComponent(label8)
																			.addComponent(label9)
																			.addComponent(label10)))
															.addComponent(targetField)))
											.addGroup(panel1Layout.createSequentialGroup().addComponent(label11)
													.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
													.addComponent(catalogCombo, GroupLayout.PREFERRED_SIZE, 96,
															GroupLayout.PREFERRED_SIZE)
													.addGap(95, 95, 95)))
									.addContainerGap()));
					panel1Layout.setVerticalGroup(panel1Layout.createParallelGroup()
							.addGroup(panel1Layout.createSequentialGroup().addContainerGap()
									.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
											.addComponent(label1).addComponent(targetField, GroupLayout.PREFERRED_SIZE,
													GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addGroup(panel1Layout.createParallelGroup()
											.addGroup(panel1Layout.createSequentialGroup().addGap(16, 16, 16)
													.addComponent(label2))
											.addGroup(panel1Layout.createSequentialGroup()
													.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
													.addGroup(panel1Layout
															.createParallelGroup(GroupLayout.Alignment.BASELINE)
															.addComponent(raField, GroupLayout.PREFERRED_SIZE,
																	GroupLayout.DEFAULT_SIZE,
																	GroupLayout.PREFERRED_SIZE)
															.addComponent(label7))))
									.addGroup(panel1Layout.createParallelGroup()
											.addGroup(panel1Layout.createSequentialGroup().addGap(16, 16, 16)
													.addComponent(label3))
											.addGroup(panel1Layout.createSequentialGroup()
													.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
													.addGroup(panel1Layout
															.createParallelGroup(GroupLayout.Alignment.BASELINE)
															.addComponent(decField, GroupLayout.PREFERRED_SIZE,
																	GroupLayout.DEFAULT_SIZE,
																	GroupLayout.PREFERRED_SIZE)
															.addComponent(label8))))
									.addGroup(panel1Layout.createParallelGroup()
											.addGroup(panel1Layout.createSequentialGroup().addGap(16, 16, 16)
													.addComponent(label4))
											.addGroup(panel1Layout.createSequentialGroup()
													.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
													.addGroup(panel1Layout
															.createParallelGroup(GroupLayout.Alignment.BASELINE)
															.addComponent(fovField, GroupLayout.PREFERRED_SIZE,
																	GroupLayout.DEFAULT_SIZE,
																	GroupLayout.PREFERRED_SIZE)
															.addComponent(label9))))
									.addGroup(panel1Layout.createParallelGroup()
											.addGroup(panel1Layout.createSequentialGroup().addGap(16, 16, 16)
													.addComponent(label5))
											.addGroup(panel1Layout.createSequentialGroup()
													.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
													.addGroup(panel1Layout
															.createParallelGroup(GroupLayout.Alignment.BASELINE)
															.addComponent(magLimitField, GroupLayout.PREFERRED_SIZE,
																	GroupLayout.DEFAULT_SIZE,
																	GroupLayout.PREFERRED_SIZE)
															.addComponent(label10))))
									.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE,
											Short.MAX_VALUE)
									.addGroup(panel1Layout.createParallelGroup().addComponent(label11).addComponent(
											catalogCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
											GroupLayout.PREFERRED_SIZE))));
				}

				// ======== panel2 ========
				{

					// ---- downLoadButton ----
					downLoadButton.setText(bundle.getString("VspDataUI.downLoadButton.text"));

					// ---- cancelButton ----
					cancelButton.setText(bundle.getString("VspDataUI.cancelButton.text"));

					GroupLayout panel2Layout = new GroupLayout(panel2);
					panel2.setLayout(panel2Layout);
					panel2Layout.setHorizontalGroup(panel2Layout.createParallelGroup().addGroup(panel2Layout
							.createSequentialGroup().addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addGroup(panel2Layout.createParallelGroup()
									.addComponent(downLoadButton, GroupLayout.Alignment.TRAILING)
									.addComponent(cancelButton, GroupLayout.Alignment.TRAILING))
							.addContainerGap()));
					panel2Layout.linkSize(SwingConstants.HORIZONTAL, new Component[] { cancelButton, downLoadButton });
					panel2Layout.setVerticalGroup(panel2Layout.createParallelGroup()
							.addGroup(panel2Layout.createSequentialGroup().addContainerGap()
									.addComponent(downLoadButton)
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(cancelButton)
									.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
				}

				// ---- label6 ----
				label6.setText(bundle.getString("VspDataUI.label6.text_2"));

				GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
				contentPanel.setLayout(contentPanelLayout);
				contentPanelLayout.setHorizontalGroup(contentPanelLayout.createParallelGroup()
						.addGroup(contentPanelLayout.createSequentialGroup().addGroup(contentPanelLayout
								.createParallelGroup()
								.addComponent(panel1, GroupLayout.PREFERRED_SIZE, 263, GroupLayout.PREFERRED_SIZE)
								.addGroup(contentPanelLayout.createSequentialGroup().addGap(29, 29, 29)
										.addComponent(label6).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(filterCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
												GroupLayout.PREFERRED_SIZE)))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
								.addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)));
				contentPanelLayout.setVerticalGroup(contentPanelLayout.createParallelGroup()
						.addGroup(contentPanelLayout.createSequentialGroup()
								.addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(label6).addComponent(filterCombo, GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGap(0, 11, Short.MAX_VALUE))
						.addGroup(contentPanelLayout.createSequentialGroup().addComponent(panel2,
								GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addContainerGap()));
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
	private JTextField targetField;
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
	private JPanel panel2;
	private JButton downLoadButton;
	private JButton cancelButton;
	private JLabel label6;
	private JComboBox<String> filterCombo;
	// JFormDesigner - End of variables declaration //GEN-END:variables

}
