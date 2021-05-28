package rfl.astroimagej.dev.catalog_ui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;


class CatalogFormVerifierTest {
	
	private static FormVerifier verifier;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		verifier = new FormVerifier();
	}

	
	@DisplayName("Verify that blank user input is invalid for all fields")
	@ParameterizedTest
	@CsvSource({ 
		"targetfield", "rafield", "decfield", "fovfield", "maglimitfield"
	})
	void testInput_EmptyString_NotValid(String fieldName) {
		String input = "";
		assertFalse(verifier.verifyInput(input, fieldName));
		
	    input = " ";
		assertFalse(verifier.verifyInput(input, fieldName));		
	}

	@DisplayName("Verify that target name comprising alphanumeric + white space, '.-_' chars is valid")
	@ParameterizedTest
	@CsvSource({ 
		"WASP12", "Wasp-12", "wasp_12", "wasp 12",
		"ABCDEFGHIJKL_mnopqrstuvwxyz -0123456789",	
	})
	void testAlphanumeric_TargetName_IsValid(String input) {
		String fieldName = "targetfield";		
		assertTrue(verifier.verifyInput(input, fieldName));
	}
	
	@DisplayName("Verify that target name containing non-alphanumeric chars, excluding white space, '.-_' is invalid")
	@ParameterizedTest
	@CsvSource({ 
		"W@SP12 ", "Wasp-12$", "wasp^^12", "wasp~12"
	})
	void testOtherThan_alphanumeric_TargetName_NotValid(String targetName) {
		String fieldName = "targetfield";
		assertFalse(verifier.verifyInput(targetName, fieldName));
	}
	
	
	@DisplayName("Verify that fov in range 1.0 - 1200.9 is valid")
	@ParameterizedTest
	@CsvSource({ 
		"1.0", "12.3", "1200.0, 1200.9"	
	})
	void testFov_In_Range(String input) {
		String fieldName = "fovfield";		
		assertTrue(verifier.verifyInput(input, fieldName));
	}
	
	
	@DisplayName("Verify that out-of-range 1.0 - 1200.9 or non-numeric fov is invalid")
	@ParameterizedTest
	@CsvSource({ 
		"0.9", "1201.0", "-1.0",
		"12.0O", "1/,23"
	})
	void testFov_Out_Of_Range(String input) {
		String fieldName = "fovfield";		
		assertFalse(verifier.verifyInput(input, fieldName));
	}
		
	@DisplayName("Verify that magLimit in range 1.0 - 99.9 is valid")
	@ParameterizedTest
	@CsvSource({ 
		"1.0", "12.3", "99.9"	
	})
	void testMagLimit_In_Range(String input) {
		String fieldName = "maglimitfield";		
		assertTrue(verifier.verifyInput(input, fieldName));
	}
	
	
	@DisplayName("Verify magLimit outside range 1.0 - 99.9 is invalid")
	@ParameterizedTest
	@CsvSource({ 
		"0.9", "100.0", "-1.0", 
		"12.0O", "1/,23" 
	})
	void testMagLimit_Out_Of_Range(String input) {
		String fieldName = "maglimitfield";		
		assertFalse(verifier.verifyInput(input, fieldName));
	}
	
	@DisplayName("Verify formatted RA inputs in range 00:00:00.00 -> 23:59:59.99 is valid")
	@ParameterizedTest
	@CsvSource({
		"+00:00:00.00", "12:34:56", "12:34:56.", "12:34:56.7", "23:59:59.99",
		"0:0:0.00", " 1: 2: 3.45"
	})
	void testVerify_InputCoords_RA_InRange_IsValid(String input) {
		assertTrue(verifier.verifyInput(input, "rafield"));
	}
	
	
	@DisplayName("Verify formatted RA input outside range 00:00:00.00 -> 23:59:59.99 is invalid")
	@ParameterizedTest
	@CsvSource({
		"24:00:00.00", "-00:00:00.01",
		"12:60:56.78", "12:34:60.78",
		"12:34", "12@34:56.78"
	})
	void testVerify_InputCoords_RA_OutOfRange_IsInvalid(String input) {
		assertFalse(verifier.verifyInput(input, "rafield"));
	}
	
	@DisplayName("Verify formatted DEC input in range ±90:59:59.99  is valid")
	@ParameterizedTest
	@CsvSource({
		"+00:00:00.00", "12:34:56", "-12:34:56",
		"+90:00:00.00", "-90:00:00.00", "+90:59:59.99", "-90:59:59.99",
		"0:0:0.00", " -1: 2: 3.45"
	})
	void testVerify_InputCoords_DEC_InRange_IsValid(String input) {
		assertTrue(verifier.verifyInput(input, "decfield"));
	}
	

	@DisplayName("Verify formatted DEC input outside range  ±91:00:00.00 is invalid")
	@ParameterizedTest
	@CsvSource({
		"91:00:00.00", "-91:00:00.00", 
		"12:60:56.78", "12:34:60.78",
		"-12:34", "+12@34:56.78", "+12:34 56.78",
	})
	void testVerify_InputCoords_DEC_OutOfRange_IsInvalid(String input) {
		assertFalse(verifier.verifyInput(input, "decfield"));
	}
	
}

