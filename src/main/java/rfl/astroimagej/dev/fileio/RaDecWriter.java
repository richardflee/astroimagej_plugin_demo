package rfl.astroimagej.dev.fileio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

import rfl.astroimagej.dev.catalogs.VspCatalog;
import rfl.astroimagej.dev.queries.CatalogQuery;
import rfl.astroimagej.dev.queries.FieldObject;
import rfl.astroimagej.dev.queries.QueryResult;
import rfl.astroimagej.dev.utils.AstroCoords;

public class RaDecWriter {

	/**
	 * Writes radec file with filename format
	 * [user_input_objectid].[magband].radec.txt
	 * <p>
	 * The result of a query on Variable Star Plotter (VSP) or other on-line catalog
	 * is an array of ref star attributes which match query criteria. This method
	 * writes data to an radec format file. Importing the radec file into
	 * astroimagej enables the software to draw photometry apertures on a
	 * plate-solved fits image.
	 * </p>
	 * <p>
	 * Ref: https://www.astro.louisville.edu/software/astroimagej/
	 * </p>
	 * 
	 * @param query  catalog query object, contains target star data
	 * @param result query result object, contains list of Field objects containing
	 *               ref star data for current filter / mag band
	 */
	public String writeRaDecFile(CatalogQuery query, QueryResult result) {
		// converts query data to string list to write to radec file
		List<String> lines = compileRaDecList(query, result);

		String message = "";
		if (result.getFieldObjects().size() == 0) {
			message = String.format("No records found in %s catalog for specified parameters", query.getCatalogType());
			return message;
		}
		
		File file = getFile(query);
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
			for (String line : lines) {
				bw.append(line);
			}
			message = String.format("Saved radec file: %s", file.toString());
		} catch (IOException e) {
			message = String.format("Error in writing file: %s", file.toString());
		}
		return message;
	}

	// compile path and filename from query fields
	// filename format <objectid>.<filter>.radec.txt
	// dir format ..\AstroImageJ\radec
	private File getFile(CatalogQuery query) {
		// filename
		String objectId = query.getObjectId();
		String filterBand = query.getMagBand();
		String filename = String.join(".", Arrays.asList(objectId, filterBand, "radec.txt"));

		// path to radec file, create new folder if necessary
		File dir = new File(System.getProperty("user.dir"), "radec");
		dir.mkdirs();
		File file = new File(dir, filename);
		return file;
	}

	// Compiles a string list of target and ref_star parms to write to radec file
	// astroimagej treats a line with leading char '#' as comment
	// data format RA (HMS), DEC (DMS), RefStar (flag), Centroid (flag), Mag
	private List<String> compileRaDecList(CatalogQuery query, QueryResult result) {
		// string list for file write, extract FieldObjects from QueryREsult object
		List<String> lines = new ArrayList<>();
		List<FieldObject> fieldObjects = result.getFieldObjects();

		// target object data
		FieldObject to = new FieldObject();
		to.setObjectId(query.getObjectId());
		to.setRaHr(query.getRaHr());
		to.setDecDeg(query.getDecDeg());
		to.setMag(99.999);

		// data block format RA (HMS), Dec (DMS), RefStar, Centroid, Mag
		// header
		lines.add("#RA, Dec, RefStar, Centroid, Mag\n");
		// target data
		lines.add(getFieldLine(to, 0, true));
		// ref star data
		for (FieldObject fo : fieldObjects) {
			lines.add(getFieldLine(fo, 0, false));
		}
		// end of data
		lines.add("#\n");

		// comment block, as data prepend aperture & object id
		lines.add("#Ap, Auid, RA, Dec, RefStar, Centroid, Mag\n");
		// target comment
		lines.add(getFieldLine(to, 1, true));
		// ref star comments
		int index = 2;
		for (FieldObject fo : fieldObjects) {
			lines.add(getFieldLine(fo, index++, false));
		}
		// append filter / mag band
		lines.add(String.format("#%s", query.getMagBand()));
		return lines;
	}

	/*
	 * compile one line of radec data or a comment line
	 * 
	 * @param fo current data object *
	 * 
	 * @param index index = 0 => write data line, otherwise write comment line
	 * 
	 * @param isTarget => true for target line (top data or comment line) otherwise
	 * false
	 * 
	 * @return compiled radec write string
	 */
	private String getFieldLine(FieldObject fo, int index, boolean isTarget) {
		// sets RefStar, Centroid flags + catalog mag for selected band (mag = 99.999
		// target line)
		String lineEnd = (isTarget) ? ", 0, " : ", 1, ";
		lineEnd += String.format("1, %s", String.valueOf(fo.getMag()));
		lineEnd += "\n";

		// Concatenate ra and dec sexagesimal values & append lineEnd string
		List<String> terms = Arrays.asList(AstroCoords.raHr_To_raHms(fo.getRaHr()),
				AstroCoords.decDeg_To_decDms(fo.getDecDeg()));
		String line = String.join(", ", terms) + lineEnd;

		// target or RefStar data line
		if (index == 0) {
			return line;
			// target comment line
		} else if (index == 1) {
			return String.format("#T%s, %s, " + line, String.valueOf(index), fo.getObjectId());
			// RefStar comment line
		} else {
			return String.format("#C%s, %s, " + line, String.valueOf(index), fo.getObjectId());
		}
	}

	public static void main(String[] args) {

		CatalogQuery query = new CatalogQuery();
		QueryResult result = null;

		VspCatalog catalog = new VspCatalog();
		result = catalog.runQuery(query);
		System.out.println(result.toString());

		RaDecWriter radec = new RaDecWriter();
		String message = radec.writeRaDecFile(query, result);

		JOptionPane.showMessageDialog(null, message, "RaDec Writer", JOptionPane.INFORMATION_MESSAGE);
	}

}
