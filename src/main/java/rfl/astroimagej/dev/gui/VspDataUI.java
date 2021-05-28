/*
 * Created by JFormDesigner on Wed Apr 07 08:13:13 BST 2021
 */

package rfl.astroimagej.dev.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
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

/**
 * @author Richard Lee
 */
public class VspDataUI extends JFrame {
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			System.err.println("Failed to initialize Windows Look-Feel");
		}
		
		EventQueue.invokeLater(() -> {
			VspDataUI gui = new VspDataUI();
			gui.setVisible(true);
		});
    }
		
	
	public VspDataUI() {
		initComponents();
		
		downLoadButton.addActionListener(e -> System.out.println("Download Me"));
		cancelButton.addActionListener(e -> System.exit(0));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		importVspProperties();
	}

	private void importVspProperties() {
		
		String homePath = Paths.get(System.getProperty("user.home")).toAbsolutePath().toString();
		String propsPath = Paths.get(homePath, ".astroimagej", "vspdemo.properties").toString();
		try (InputStream input = new FileInputStream(propsPath)) {

            Properties prop = new Properties();
            prop.load(input);
            
            targetField.setText(prop.getProperty("target").toString());
            raField.setText(prop.getProperty("ra").toString());
            decField.setText(prop.getProperty("dec").toString());
            magLimitField.setText(prop.getProperty("magLimit").toString());
            
            filterCombo.removeAllItems();
            String[] s = prop.getProperty("vspfilters").toString().split("\\.");
            List<String> comboItems = 
            		Arrays.asList(prop.getProperty("vspfilters").toString().split("\\."));
            
            for (String item: comboItems) {
            	filterCombo.addItem(item);
            }

            filterCombo.setSelectedItem(prop.getProperty("filter").toString());

        } catch (IOException ex) {
            ex.printStackTrace();
        }		
	}


	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
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
		label6 = new JLabel();
		filterCombo = new JComboBox<>();
		label7 = new JLabel();
		label8 = new JLabel();
		label9 = new JLabel();
		label10 = new JLabel();
		panel2 = new JPanel();
		downLoadButton = new JButton();
		cancelButton = new JButton();

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

					//---- targetField ----
					targetField.setText(bundle.getString("VspDataUI.targetField.text"));
					targetField.setFocusCycleRoot(true);
					targetField.setNextFocusableComponent(raField);

					//---- label2 ----
					label2.setText(bundle.getString("VspDataUI.label2.text_3"));

					//---- raField ----
					raField.setText("06:30:32.80");
					raField.setNextFocusableComponent(decField);

					//---- label3 ----
					label3.setText(bundle.getString("VspDataUI.label3.text_3"));

					//---- decField ----
					decField.setText("+20:40:20.27");
					decField.setNextFocusableComponent(fovField);

					//---- label4 ----
					label4.setText(bundle.getString("VspDataUI.label4.text_3"));

					//---- fovField ----
					fovField.setText("60.0");
					fovField.setNextFocusableComponent(magLimitField);

					//---- label5 ----
					label5.setText(bundle.getString("VspDataUI.label5.text_3"));

					//---- magLimitField ----
					magLimitField.setText(bundle.getString("VspDataUI.magLimitField.text"));
					magLimitField.setNextFocusableComponent(filterCombo);

					//---- label6 ----
					label6.setText(bundle.getString("VspDataUI.label6.text_2"));

					//---- filterCombo ----
					filterCombo.setModel(new DefaultComboBoxModel<>(new String[] {
						"V",
						"B",
						"Rc",
						"Ic"
					}));
					filterCombo.setNextFocusableComponent(downLoadButton);

					//---- label7 ----
					label7.setText(bundle.getString("VspDataUI.label7.text_2"));

					//---- label8 ----
					label8.setText(bundle.getString("VspDataUI.label8.text_2"));

					//---- label9 ----
					label9.setText(bundle.getString("VspDataUI.label9.text_2"));

					//---- label10 ----
					label10.setText(bundle.getString("VspDataUI.label10.text_2"));

					GroupLayout panel1Layout = new GroupLayout(panel1);
					panel1.setLayout(panel1Layout);
					panel1Layout.setHorizontalGroup(
						panel1Layout.createParallelGroup()
							.addGroup(panel1Layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
									.addComponent(label1)
									.addComponent(label2)
									.addComponent(label3)
									.addComponent(label4)
									.addComponent(label5)
									.addComponent(label6))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
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
									.addComponent(filterCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(targetField))
								.addContainerGap())
					);
					panel1Layout.setVerticalGroup(
						panel1Layout.createParallelGroup()
							.addGroup(panel1Layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(label1)
									.addComponent(targetField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
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
								.addGap(18, 18, 18)
								.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(label6)
									.addComponent(filterCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addContainerGap())
					);
				}

				//======== panel2 ========
				{

					//---- downLoadButton ----
					downLoadButton.setText(bundle.getString("VspDataUI.downLoadButton.text"));
					downLoadButton.setNextFocusableComponent(cancelButton);

					//---- cancelButton ----
					cancelButton.setText(bundle.getString("VspDataUI.cancelButton.text"));
					cancelButton.setNextFocusableComponent(targetField);

					GroupLayout panel2Layout = new GroupLayout(panel2);
					panel2.setLayout(panel2Layout);
					panel2Layout.setHorizontalGroup(
						panel2Layout.createParallelGroup()
							.addGroup(panel2Layout.createSequentialGroup()
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(panel2Layout.createParallelGroup()
									.addComponent(downLoadButton, GroupLayout.Alignment.TRAILING)
									.addComponent(cancelButton, GroupLayout.Alignment.TRAILING))
								.addContainerGap())
					);
					panel2Layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {cancelButton, downLoadButton});
					panel2Layout.setVerticalGroup(
						panel2Layout.createParallelGroup()
							.addGroup(panel2Layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(downLoadButton)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(cancelButton)
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					);
				}

				GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
				contentPanel.setLayout(contentPanelLayout);
				contentPanelLayout.setHorizontalGroup(
					contentPanelLayout.createParallelGroup()
						.addGroup(contentPanelLayout.createSequentialGroup()
							.addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				);
				contentPanelLayout.setVerticalGroup(
					contentPanelLayout.createParallelGroup()
						.addGroup(contentPanelLayout.createSequentialGroup()
							.addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addComponent(panel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				);
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
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
	private JLabel label6;
	private JComboBox<String> filterCombo;
	private JLabel label7;
	private JLabel label8;
	private JLabel label9;
	private JLabel label10;
	private JPanel panel2;
	private JButton downLoadButton;
	private JButton cancelButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
