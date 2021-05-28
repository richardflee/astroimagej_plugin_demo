package rfl.astroimagej.dev.catalog_ui;

import java.awt.Color;

import javax.swing.JTextField;

import rfl.astroimagej.dev.utils.AstroCoords;
import rfl.astroimagej.dev.utils.CatalogUtils;
import rfl.astroimagej.dev.utils.Radec;

public class InputValidation {

	private JTextField targetField;
	private JTextField raField;
	private JTextField decField;
	private JTextField fovField;
	private JTextField magLimitField;

	public InputValidation(JTextField targetField, JTextField raField, JTextField decField, JTextField fovField,
			JTextField magLimitField) {
		super();
		this.targetField = targetField;
		this.raField = raField;
		this.decField = decField;
		this.fovField = fovField;
		this.magLimitField = magLimitField;
	}

	protected boolean validateAllInputs() {
		return validateInput(raField) && validateInput(decField) && validateInput(fovField) && validateInput(magLimitField);
	}

	protected boolean validateInput(JTextField textField) {

		textField.setForeground(Color.black);
		String input = textField.getText();
		input = input.trim();

		if (textField.equals(targetField)) {
			if (CatalogUtils.isValidTargetName(input)) {
				targetField.setText(input);
				raField.requestFocus();
				return true;
			}
		}

		if (textField.equals(raField)) {
			if (CatalogUtils.isValidCoords(input, Radec.RA)) {
				input = AstroCoords.filterCoords(input, Radec.RA);
				raField.setText(input);
				decField.requestFocus();
				return true;
			}
		}

		if (textField.equals(decField)) {
			if (CatalogUtils.isValidCoords(input, Radec.DEC)) {
				input = AstroCoords.filterCoords(input, Radec.DEC);
				decField.setText(input);
				fovField.requestFocus();
				return true;
			}
		}

		if (textField.equals(fovField)) {
			if (CatalogUtils.isValidFov(input)) {
				fovField.setText(String.format("%.1f", Double.parseDouble(input)));
				magLimitField.requestFocus();
				return true;
			}
		}

		if (textField.equals(magLimitField)) {
			if (CatalogUtils.isValidMagLimit(input)) {
				magLimitField.setText(String.format("%.1f", Double.parseDouble(input)));
				targetField.requestFocus();
				return true;
			}
		}

		textField.setForeground(Color.red);
		return false;
	}
}
