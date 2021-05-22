package rfl.astroimagej.dev.catalog_ui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import rfl.astroimagej.dev.enums.TextFieldType;

class TextFieldVerifierTest {

	private static TextInputVerifier verifier;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		verifier = new TextInputVerifier();
	}

	@DisplayName("Verify that blank user input is invalid for all fields")
	@Test
	void testInput_EmptyString_NotValid() {
		String input = "";
		// loop through all fields
		for (TextFieldType dataType : TextFieldType.values()) {
			assertFalse(verifier.verifyInput(input, dataType));
		}
	}

	@DisplayName("Verify that target name comprising alphanumeric + white space, '.-_' chars is valid")
	@ParameterizedTest
	@CsvSource({ "WASP12", "Wasp-12", "wasp_12", "wasp 12", "ABCDEFGHIJKL_mnopqrstuvwxyz -0123456789", })
	void testAlphanumeric_TargetName_IsValid(String input) {
		TextFieldType dataType = TextFieldType.OBJECT_ID;
		assertTrue(verifier.verifyInput(input, dataType));
	}

	@DisplayName("Verify that target name containing non-alphanumeric chars, excluding white space, '.-_' is invalid")
	@ParameterizedTest
	@CsvSource({ "W@SP12 ", "Wasp-12$", "wasp^^12", "wasp~12" })
	void testOtherThan_alphanumeric_TargetName_NotValid(String targetName) {
		TextFieldType dataType = TextFieldType.OBJECT_ID;
		assertFalse(verifier.verifyInput(targetName, dataType));
	}

	@DisplayName("Verify that fov in range 1.0 - 1199.9 is valid")
	@ParameterizedTest
	@CsvSource({ "1.0", "12.3", "1199.9"})
	void testFov_In_Range(String input) {
		TextFieldType dataType = TextFieldType.FOV_AMIN;
		assertTrue(verifier.verifyInput(input, dataType));
	}

	@DisplayName("Verify that out-of-range 1.0 - 1200 or non-numeric fov is invalid")
	@ParameterizedTest
	@CsvSource({ "0.9", "1200", "-1.0", "12.0O", "1/,23" })
	void testFov_Out_Of_Range(String input) {
		TextFieldType dataType = TextFieldType.FOV_AMIN;
		assertFalse(verifier.verifyInput(input, dataType));
	}

	@DisplayName("Verify that magLimit in range 1.0 - 99.9 is valid")
	@ParameterizedTest
	@CsvSource({ "1.0", "12.3", "99.9" })
	void testMagLimit_In_Range(String input) {
		TextFieldType dataType = TextFieldType.MAG_LIMIT;
		assertTrue(verifier.verifyInput(input, dataType));
	}

	@DisplayName("Verify magLimit outside range 1.0 - 99.9 is invalid")
	@ParameterizedTest
	@CsvSource({ "0.9", "100.0", "-1.0", "12.0O", "1/,23" })
	void testMagLimit_Out_Of_Range(String input) {
		TextFieldType dataType = TextFieldType.MAG_LIMIT;
		assertFalse(verifier.verifyInput(input, dataType));
	}

	@DisplayName("Verify formatted RA inputs in range 00:00:00.00 -> 23:59:59.99 is valid")
	@ParameterizedTest
	@CsvSource({ "+00:00:00.00", "12:34:56", "12:34:56.", "12:34:56.7", "23:59:59.99", "0:0:0.00", " 1: 2: 3.45" })
	void testVerify_InputCoords_RA_InRange_IsValid(String input) {
		TextFieldType dataType = TextFieldType.RA_HMS;
		assertTrue(verifier.verifyInput(input, dataType));
	}

	@DisplayName("Verify formatted RA input outside range 00:00:00.00 -> 23:59:59.99 is invalid")
	@ParameterizedTest
	@CsvSource({ "24:00:00.00", "-00:00:00.01", "12:60:56.78", "12:34:60.78", "12:34", "12@34:56.78" })
	void testVerify_InputCoords_RA_OutOfRange_IsInvalid(String input) {
		TextFieldType dataType = TextFieldType.RA_HMS;
		assertFalse(verifier.verifyInput(input, dataType));
	}

	@DisplayName("Verify formatted DEC input in range ±90:59:59.99  is valid")
	@ParameterizedTest
	@CsvSource({ "+00:00:00.00", "12:34:56", "-12:34:56", "+90:00:00.00", "-90:00:00.00", "+90:59:59.99",
			"-90:59:59.99", "0:0:0.00", " -1: 2: 3.45" })
	void testVerify_InputCoords_DEC_InRange_IsValid(String input) {
		TextFieldType dataType = TextFieldType.DEC_DMS;
		assertTrue(verifier.verifyInput(input, dataType));
	}

	@DisplayName("Verify formatted DEC input outside range  ±91:00:00.00 is invalid")
	@ParameterizedTest
	@CsvSource({ "91:00:00.00", "-91:00:00.00", "12:60:56.78", "12:34:60.78", "-12:34", "+12@34:56.78",
			"+12:34 56.78", })
	void testVerify_InputCoords_DEC_OutOfRange_IsInvalid(String input) {
		TextFieldType dataType = TextFieldType.DEC_DMS;
		assertFalse(verifier.verifyInput(input, dataType));
	}
}
